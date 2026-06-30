/*
 * This file is part of the Carpet TIS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
 *
 * Carpet TIS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet TIS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet TIS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */

package carpettisaddition.mixins.logger.lightscheduledebug;

import carpettisaddition.CarpetTISAdditionSettings;
import carpettisaddition.logging.loggers.lightscheduledebug.ILightScheduleDebug;
import carpettisaddition.logging.loggers.lightscheduledebug.LightScheduleDebugBatchData;
import carpettisaddition.logging.loggers.lightscheduledebug.LightScheduleDebugHUDLogger;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.IntSupplier;

@Mixin(ThreadedLevelLightEngine.class)
public abstract class LightScheduleDebugMixin implements ILightScheduleDebug
{
	@Shadow @Final private ProcessorMailbox<Runnable> taskMailbox;
	@Shadow @Final private ObjectList<Pair<ThreadedLevelLightEngine.TaskType, Runnable>> lightTasks;

	@Unique private final AtomicLong runUpdateCount = new AtomicLong();
	@Unique private final AtomicLong runUpdateFromMailbox = new AtomicLong();
	@Unique private final AtomicLong runUpdateFromTrySchedule = new AtomicLong();
	@Unique private final AtomicLong tryScheduleUpdateCount = new AtomicLong();
	@Unique private final AtomicBoolean nextRunUpdateFromTrySchedule = new AtomicBoolean(false);
	@Unique private final AtomicLong runUpdateTotalNanos = new AtomicLong();
	@Unique private final AtomicLong phaseANanos = new AtomicLong();
	@Unique private final AtomicLong phaseBNanos = new AtomicLong();
	@Unique private final AtomicLong phaseCNanos = new AtomicLong();
	@Unique private final AtomicLong lightTasksSizeSum = new AtomicLong();
	@Unique private long runUpdateStartTime;
	@Unique private long phaseBStartTime;
	@Unique private long phaseCStartTime;
	@Unique private final ObjectList<LightTaskDebugInfo> lightTaskDebugInfos = new ObjectArrayList<>();
	@Unique private final AtomicLong runUpdateId = new AtomicLong();
	@Unique private volatile boolean batchCsvEnabled = false;
	@Unique private int lastRunUpdateBatchSize = 0;
	@Unique private long currentRunUpdateId = 0;

	@Redirect(
			method = "addTask(IILjava/util/function/IntSupplier;Lnet/minecraft/server/level/ThreadedLevelLightEngine$TaskType;Ljava/lang/Runnable;)V",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/server/level/ChunkTaskPriorityQueueSorter;message(Ljava/lang/Runnable;JLjava/util/function/IntSupplier;)Lnet/minecraft/server/level/ChunkTaskPriorityQueueSorter$Message;"
			)
	)
	private ChunkTaskPriorityQueueSorter.Message<Runnable> wrapSorterMessage(Runnable runnable, long chunkPos, IntSupplier levelSupplier)
	{
		return ChunkTaskPriorityQueueSorter.message(() -> {
			int level = levelSupplier.getAsInt();
			this.lightTaskDebugInfos.add(new LightTaskDebugInfo(chunkPos, level));
			runnable.run();
		}, chunkPos, levelSupplier);
	}

	@Inject(method = "tryScheduleUpdate", at = @At("HEAD"), cancellable = true)
	private void onTryScheduleUpdate(CallbackInfo ci)
	{
		this.tryScheduleUpdateCount.getAndIncrement();
		if (CarpetTISAdditionSettings.lightScheduleDebugDisableTrySchedule)
		{
			ci.cancel();
		}
	}

	@Inject(
			method = "tryScheduleUpdate",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/util/thread/ProcessorMailbox;tell(Ljava/lang/Object;)V"
			)
	)
	private void onTryScheduleCASSuccess(CallbackInfo ci)
	{
		this.nextRunUpdateFromTrySchedule.set(true);
	}

	@Inject(method = "runUpdate", at = @At("HEAD"))
	private void onRunUpdateHead(CallbackInfo ci)
	{
		this.runUpdateStartTime = System.nanoTime();
		this.currentRunUpdateId = this.runUpdateId.incrementAndGet();
		this.runUpdateCount.getAndIncrement();
		this.lightTasksSizeSum.addAndGet(this.lightTasks.size());
		this.lastRunUpdateBatchSize = Math.min(this.lightTasks.size(), 1000);
		if (this.batchCsvEnabled && this.lastRunUpdateBatchSize > 0)
		{
			this.recordBatchSummary(this.currentRunUpdateId, this.lastRunUpdateBatchSize);
		}
		if (this.nextRunUpdateFromTrySchedule.compareAndSet(true, false))
		{
			this.runUpdateFromTrySchedule.getAndIncrement();
		}
		else
		{
			this.runUpdateFromMailbox.getAndIncrement();
		}
	}

	@Inject(
			method = "runUpdate",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/lighting/LevelLightEngine;runLightUpdates()I"
			)
	)
	private void onPhaseAEnd(CallbackInfo ci)
	{
		long now = System.nanoTime();
		this.phaseANanos.addAndGet(now - this.runUpdateStartTime);
		this.phaseBStartTime = now;
	}

	@Inject(
			method = "runUpdate",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/world/level/lighting/LevelLightEngine;runLightUpdates()I",
					shift = At.Shift.AFTER
			)
	)
	private void onPhaseBEnd(CallbackInfo ci)
	{
		long now = System.nanoTime();
		this.phaseBNanos.addAndGet(now - this.phaseBStartTime);
		this.phaseCStartTime = now;
	}

	@Inject(method = "runUpdate", at = @At("TAIL"))
	private void onRunUpdateTail(CallbackInfo ci)
	{
		long now = System.nanoTime();
		this.phaseCNanos.addAndGet(now - this.phaseCStartTime);
		this.runUpdateTotalNanos.addAndGet(now - this.runUpdateStartTime);
		for (int i = 0; i < this.lastRunUpdateBatchSize && !this.lightTaskDebugInfos.isEmpty(); i++)
		{
			this.lightTaskDebugInfos.remove(0);
		}
	}

	@Unique
	private void recordBatchSummary(long runUpdateId, int batchSize)
	{
		Map<Long, Integer> chunkCounts = new HashMap<>();
		int[] levelCounts = new int[12];
		int otherLevelCount = 0;
		int limit = Math.min(batchSize, this.lightTaskDebugInfos.size());
		for (int i = 0; i < limit; i++)
		{
			LightTaskDebugInfo info = this.lightTaskDebugInfos.get(i);
			Integer count = chunkCounts.get(info.chunkPos);
			chunkCounts.put(info.chunkPos, count == null ? 1 : count + 1);
			if (info.level >= 22 && info.level <= 33)
			{
				levelCounts[info.level - 22]++;
			}
			else
			{
				otherLevelCount++;
			}
		}

		String[] topChunks = new String[]{"", "", "", "", ""};
		int[] topCounts = new int[5];
		for (Map.Entry<Long, Integer> entry : chunkCounts.entrySet())
		{
			for (int i = 0; i < 5; i++)
			{
					if (entry.getValue() > topCounts[i])
					{
						for (int j = 4; j > i; j--)
					{
						topCounts[j] = topCounts[j - 1];
						topChunks[j] = topChunks[j - 1];
					}
					topCounts[i] = entry.getValue();
					topChunks[i] = ChunkPos.getX(entry.getKey()) + ":" + ChunkPos.getZ(entry.getKey());
					break;
				}
			}
		}
		int maxChunkTasks = 0;
		for (int count : chunkCounts.values())
		{
			maxChunkTasks = Math.max(maxChunkTasks, count);
		}
		LightScheduleDebugHUDLogger.getInstance().writeBatchCsvLine(new LightScheduleDebugBatchData(runUpdateId, limit, chunkCounts.size(), maxChunkTasks, topChunks, topCounts, levelCounts, otherLevelCount));
	}

	@Override
	public long getRunUpdateCountAndClean$TISCM()
	{
		return this.runUpdateCount.getAndSet(0);
	}

	@Override
	public long getRunUpdateFromMailboxCountAndClean$TISCM()
	{
		return this.runUpdateFromMailbox.getAndSet(0);
	}

	@Override
	public long getRunUpdateFromTryScheduleCountAndClean$TISCM()
	{
		return this.runUpdateFromTrySchedule.getAndSet(0);
	}

	@Override
	public long getTryScheduleUpdateCountAndClean$TISCM()
	{
		return this.tryScheduleUpdateCount.getAndSet(0);
	}

	@Override
	public long getLightMailboxRunCountAndClean$TISCM()
	{
		return 0;
	}

	@Override
	public long getSorterMailboxRunCountAndClean$TISCM()
	{
		return 0;
	}

	@Override
	public long getSorterPollTaskCountAndClean$TISCM()
	{
		return 0;
	}

	@Override
	public long getRunUpdateNanosAndClean$TISCM()
	{
		return this.runUpdateTotalNanos.getAndSet(0);
	}

	@Override
	public long getPhaseANanosAndClean$TISCM()
	{
		return this.phaseANanos.getAndSet(0);
	}

	@Override
	public long getPhaseBNanosAndClean$TISCM()
	{
		return this.phaseBNanos.getAndSet(0);
	}

	@Override
	public long getPhaseCNanosAndClean$TISCM()
	{
		return this.phaseCNanos.getAndSet(0);
	}

	@Override
	public long getLightTasksSizeSumAndClean$TISCM()
	{
		return this.lightTasksSizeSum.getAndSet(0);
	}

	@Override
	public long getLightMailboxQueueSize$TISCM()
	{
		//#if MC >= 11700
		//$$ return this.taskMailbox.size();
		//#else
		return 0;
		//#endif
	}

	@Override
	public void setBatchCsvEnabled$TISCM(boolean enabled)
	{
		this.batchCsvEnabled = enabled;
	}

	@Unique
	private static class LightTaskDebugInfo
	{
		private final long chunkPos;
		private final int level;

		private LightTaskDebugInfo(long chunkPos, int level)
		{
			this.chunkPos = chunkPos;
			this.level = level;
		}
	}
}
