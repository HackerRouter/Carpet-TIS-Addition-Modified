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

package carpettisaddition.logging.loggers.lightscheduledebug;

import carpettisaddition.logging.TISAdditionLoggerRegistry;
import carpettisaddition.logging.loggers.AbstractHUDLogger;
import carpettisaddition.logging.loggers.lightqueue.LightQueueHUDLogger;
import carpettisaddition.logging.loggers.lightqueue.LightQueueSnapshot;
import carpettisaddition.utils.EntityUtils;
import carpettisaddition.utils.Messenger;
import carpettisaddition.utils.compat.DimensionWrapper;
import com.google.common.collect.Maps;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.lighting.LevelLightEngine;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicLong;

public class LightScheduleDebugHUDLogger extends AbstractHUDLogger
{
	public static final String NAME = "lightScheduleDebug";
	private static final LightScheduleDebugHUDLogger INSTANCE = new LightScheduleDebugHUDLogger();
	private final Map<ServerLevel, LightScheduleDebugWindowedDataRecorder> dataMap = Maps.newHashMap();
	private final Map<ResourceLocation, ServerLevel> nameToWorldMap = Maps.newHashMap();

	private final AtomicLong globalLightMailboxRun = new AtomicLong();
	private final AtomicLong globalSorterMailboxRun = new AtomicLong();
	private final AtomicLong globalSorterPollTask = new AtomicLong();
	private final AtomicLong globalBlockEngineNanos = new AtomicLong();
	private final AtomicLong globalSkyEngineNanos = new AtomicLong();
	private final AtomicLong globalLightUpdatesResult = new AtomicLong();
	private final AtomicLong globalBlockNodesToCheckSize = new AtomicLong();
	private final AtomicLong globalDecreaseNodes = new AtomicLong();
	private final AtomicLong globalIncreaseNodes = new AtomicLong();
	private final AtomicLong globalBlkChkSize = new AtomicLong();
	private final AtomicLong globalSkyChkSize = new AtomicLong();
	private final AtomicLong globalBlkDec = new AtomicLong();
	private final AtomicLong globalBlkInc = new AtomicLong();
	private final AtomicLong globalSkyDec = new AtomicLong();
	private final AtomicLong globalSkyInc = new AtomicLong();
	private final AtomicLong globalSectionHit = new AtomicLong();
	private final AtomicLong globalSectionMiss = new AtomicLong();
	private final AtomicLong globalChunkCacheTotal = new AtomicLong();
	private final AtomicLong globalChunkCacheMiss = new AtomicLong();
	private PrintWriter csvWriter = null;
	private PrintWriter batchCsvWriter = null;
	private long csvStartMillis = 0;
	private long csvEndMillis = 0;
	private long csvTickCount = 0;
	private boolean forceRecording = false;

	public LightScheduleDebugHUDLogger()
	{
		super(NAME, false);
	}

	public static LightScheduleDebugHUDLogger getInstance()
	{
		return INSTANCE;
	}

	public void incrementLightMailboxRun()
	{
		this.globalLightMailboxRun.getAndIncrement();
	}

	public void incrementSorterMailboxRun()
	{
		this.globalSorterMailboxRun.getAndIncrement();
	}

	public void incrementSorterPollTask()
	{
		this.globalSorterPollTask.getAndIncrement();
	}

	public void addBlockEngineNanos(long nanos)
	{
		this.globalBlockEngineNanos.addAndGet(nanos);
	}

	public void addSkyEngineNanos(long nanos)
	{
		this.globalSkyEngineNanos.addAndGet(nanos);
	}

	public void addRunLightUpdatesResult(int result)
	{
		this.globalLightUpdatesResult.addAndGet(result);
	}

	public void addBlockNodesToCheckSize(int size)
	{
		this.globalBlockNodesToCheckSize.addAndGet(size);
	}

	public void addDecreaseNodes(int count)
	{
		this.globalDecreaseNodes.addAndGet(count);
	}

	public void addIncreaseNodes(int count)
	{
		this.globalIncreaseNodes.addAndGet(count);
	}

	public void addBlkChkSize(int size)
	{
		this.globalBlkChkSize.addAndGet(size);
	}

	public void addSkyChkSize(int size)
	{
		this.globalSkyChkSize.addAndGet(size);
	}

	public void addBlkDec(int count)
	{
		this.globalBlkDec.addAndGet(count);
	}

	public void addBlkInc(int count)
	{
		this.globalBlkInc.addAndGet(count);
	}

	public void addSkyDec(int count)
	{
		this.globalSkyDec.addAndGet(count);
	}

	public void addSkyInc(int count)
	{
		this.globalSkyInc.addAndGet(count);
	}

	public void incrementSectionHit()
	{
		this.globalSectionHit.getAndIncrement();
	}

	public void incrementSectionMiss()
	{
		this.globalSectionMiss.getAndIncrement();
	}

	public void incrementChunkCacheTotal()
	{
		this.globalChunkCacheTotal.getAndIncrement();
	}

	public void incrementChunkCacheMiss()
	{
		this.globalChunkCacheMiss.getAndIncrement();
	}

	public synchronized void startCsvSession(long durationMillis)
	{
		this.closeCsv();
		long now = System.currentTimeMillis();
		this.csvStartMillis = now;
		this.csvEndMillis = now + durationMillis;
		this.csvTickCount = 0;
		this.forceRecording = true;
		LightQueueHUDLogger.getInstance().setForceRecording(true);
		String suffix = new SimpleDateFormat("HH-mm-ss-SSS").format(new Date(now));
		try
		{
			this.csvWriter = new PrintWriter(new FileWriter(new File("light_schedule_debug_" + suffix + ".csv"), false));
			this.csvWriter.println("recordMs,lqNet,lqSize,lqIn,tick,runUpd,runUpdMbx,runUpdTrS,trySched,lightMbx,sorterMbx,sorterPoll,avgRunUpdMs,phaseAMs,phaseBMs,phaseCMs,blkMs,skyMs,nodes,blkChk,blkDec,blkInc,skyChk,skyDec,skyInc,secHit,secMiss,chkHitPct,chkMiss,chkTotal,qLen,lMbxQ,execAct");
			this.batchCsvWriter = new PrintWriter(new FileWriter(new File("light_schedule_debug_batches_" + suffix + ".csv"), false));
			this.batchCsvWriter.println("recordMs,runUpdateId,batchSize,uniqueChunks,maxChunkTasks,top1Chunk,top1Count,top2Chunk,top2Count,top3Chunk,top3Count,top4Chunk,top4Count,top5Chunk,top5Count,l22,l23,l24,l25,l26,l27,l28,l29,l30,l31,l32,l33,lOther");
		}
		catch (IOException e)
		{
			e.printStackTrace();
			this.closeCsv();
		}
	}

	public synchronized boolean isCsvSessionActive()
	{
		if (this.csvWriter == null || this.batchCsvWriter == null)
		{
			return false;
		}
		if (System.currentTimeMillis() >= this.csvEndMillis)
		{
			this.closeCsv();
			return false;
		}
		return true;
	}

	private synchronized long getRecordMillis()
	{
		return Math.max(0, System.currentTimeMillis() - this.csvStartMillis);
	}

	private synchronized void closeCsv()
	{
		if (this.csvWriter != null)
		{
			this.csvWriter.close();
			this.csvWriter = null;
		}
		if (this.batchCsvWriter != null)
		{
			this.batchCsvWriter.close();
			this.batchCsvWriter = null;
		}
		this.forceRecording = false;
		LightQueueHUDLogger.getInstance().setForceRecording(false);
	}

	public void closeCsvSession()
	{
		this.closeCsv();
	}

	public synchronized void writeBatchCsvLine(LightScheduleDebugBatchData data)
	{
		if (!this.isCsvSessionActive()) return;
		if (this.batchCsvWriter == null) return;
		this.batchCsvWriter.printf("%d,%d,%d,%d,%d", this.getRecordMillis(), data.runUpdateId, data.batchSize, data.uniqueChunks, data.maxChunkTasks);
		for (int i = 0; i < 5; i++)
		{
			String chunk = i < data.topChunks.length ? data.topChunks[i] : "";
			int count = i < data.topCounts.length ? data.topCounts[i] : 0;
			this.batchCsvWriter.printf(",%s,%d", chunk, count);
		}
		for (int i = 0; i < data.levelCounts.length; i++)
		{
			this.batchCsvWriter.printf(",%d", data.levelCounts[i]);
		}
		this.batchCsvWriter.printf(",%d%n", data.otherLevelCount);
		this.batchCsvWriter.flush();
	}

	private synchronized void writeCsvLine(LightScheduleDebugWindowedDataRecorder recorder, long lightMbxQueueSize, int execActiveThreads)
	{
		if (!this.isCsvSessionActive()) return;
		if (this.csvWriter == null) return;
		Deque<LightScheduleDebugRecordedData> deque = recorder.getQueue();
		if (deque.isEmpty()) return;

		this.csvTickCount++;
		int size = deque.size();
		long totalRunUpdates = recorder.getRunUpdateCount();

		double runUpd = (double)recorder.getRunUpdateCount() / size;
		double runUpdMbx = (double)recorder.getRunUpdateFromMailbox() / size;
		double runUpdTrS = (double)recorder.getRunUpdateFromTrySchedule() / size;
		double trySched = (double)recorder.getTryScheduleUpdateCount() / size;
		double lightMbx = (double)recorder.getLightMailboxRunCount() / size;
		double sorterMbx = (double)recorder.getSorterMailboxRunCount() / size;
		double sorterPoll = (double)recorder.getSorterPollTaskCount() / size;
		double avgRunUpdMs = totalRunUpdates > 0 ? (double)recorder.getRunUpdateNanos() / totalRunUpdates / 1_000_000.0 : 0;
		double phaseAMs = totalRunUpdates > 0 ? (double)recorder.getPhaseANanos() / totalRunUpdates / 1_000_000.0 : 0;
		double phaseBMs = totalRunUpdates > 0 ? (double)recorder.getPhaseBNanos() / totalRunUpdates / 1_000_000.0 : 0;
		double phaseCMs = totalRunUpdates > 0 ? (double)recorder.getPhaseCNanos() / totalRunUpdates / 1_000_000.0 : 0;
		double blkMs = totalRunUpdates > 0 ? (double)recorder.getBlockEngineNanos() / totalRunUpdates / 1_000_000.0 : 0;
		double skyMs = totalRunUpdates > 0 ? (double)recorder.getSkyEngineNanos() / totalRunUpdates / 1_000_000.0 : 0;
		double nodes = totalRunUpdates > 0 ? (double)recorder.getLightUpdatesResult() / totalRunUpdates : 0;
		double blkChk = totalRunUpdates > 0 ? (double)recorder.getBlkChkSize() / totalRunUpdates : 0;
		double blkDec = totalRunUpdates > 0 ? (double)recorder.getBlkDec() / totalRunUpdates : 0;
		double blkInc = totalRunUpdates > 0 ? (double)recorder.getBlkInc() / totalRunUpdates : 0;
		double skyChk = totalRunUpdates > 0 ? (double)recorder.getSkyChkSize() / totalRunUpdates : 0;
		double skyDec = totalRunUpdates > 0 ? (double)recorder.getSkyDec() / totalRunUpdates : 0;
		double skyInc = totalRunUpdates > 0 ? (double)recorder.getSkyInc() / totalRunUpdates : 0;
		double secHit = totalRunUpdates > 0 ? (double)recorder.getSectionHit() / totalRunUpdates : 0;
		double secMiss = totalRunUpdates > 0 ? (double)recorder.getSectionMiss() / totalRunUpdates : 0;
		double chkHitPct = recorder.getChunkCacheTotal() > 0 ? 100.0 * (1.0 - (double)recorder.getChunkCacheMiss() / recorder.getChunkCacheTotal()) : 0;
		double chkMiss = totalRunUpdates > 0 ? (double)recorder.getChunkCacheMiss() / totalRunUpdates : 0;
		double chkTotal = totalRunUpdates > 0 ? (double)recorder.getChunkCacheTotal() / totalRunUpdates : 0;
		double qLen = totalRunUpdates > 0 ? (double)recorder.getLightTasksSizeSum() / totalRunUpdates : 0;

		LightQueueSnapshot lq = LightQueueHUDLogger.getInstance().getOverworldSnapshot();
		this.csvWriter.printf("%d,%.2f,%d,%.2f,%d,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.1f,%.2f,%.1f,%.1f,%.1f,%d,%d%n",
				this.getRecordMillis(), lq.net, lq.size, lq.input,
				this.csvTickCount, runUpd, runUpdMbx, runUpdTrS, trySched, lightMbx, sorterMbx, sorterPoll,
				avgRunUpdMs, phaseAMs, phaseBMs, phaseCMs, blkMs, skyMs, nodes,
				blkChk, blkDec, blkInc, skyChk, skyDec, skyInc,
				secHit, secMiss, chkHitPct, chkMiss, chkTotal, qLen,
				lightMbxQueueSize, execActiveThreads
		);
		this.csvWriter.flush();
	}

	public void attachServer(MinecraftServer minecraftServer)
	{
		this.dataMap.clear();
		this.nameToWorldMap.clear();
		for (ServerLevel world : minecraftServer.getAllLevels())
		{
			this.dataMap.put(world, new LightScheduleDebugWindowedDataRecorder());
			this.nameToWorldMap.put(DimensionWrapper.of(world).getIdentifier(), world);
		}
	}

	@Override
	public String[] getSuggestedLoggingOption()
	{
		return new String[]{"dynamic", "overworld", "the_nether", "the_end"};
	}

	public void tick()
	{
		this.isCsvSessionActive();
		long lightMbx = this.globalLightMailboxRun.getAndSet(0);
		long sorterMbx = this.globalSorterMailboxRun.getAndSet(0);
		long sorterPoll = this.globalSorterPollTask.getAndSet(0);
		long blockEngNanos = this.globalBlockEngineNanos.getAndSet(0);
		long skyEngNanos = this.globalSkyEngineNanos.getAndSet(0);
		long lightUpdResult = this.globalLightUpdatesResult.getAndSet(0);
		long blockNodesCheck = this.globalBlockNodesToCheckSize.getAndSet(0);
		long decreaseNodes = this.globalDecreaseNodes.getAndSet(0);
		long increaseNodes = this.globalIncreaseNodes.getAndSet(0);
		long blkChk = this.globalBlkChkSize.getAndSet(0);
		long skyChk = this.globalSkyChkSize.getAndSet(0);
		long blkDec = this.globalBlkDec.getAndSet(0);
		long blkInc = this.globalBlkInc.getAndSet(0);
		long skyDec = this.globalSkyDec.getAndSet(0);
		long skyInc = this.globalSkyInc.getAndSet(0);
		long secHit = this.globalSectionHit.getAndSet(0);
		long secMiss = this.globalSectionMiss.getAndSet(0);
		long chunkTotal = this.globalChunkCacheTotal.getAndSet(0);
		long chunkMiss = this.globalChunkCacheMiss.getAndSet(0);
		int activeThreads = 0;
		if (net.minecraft.Util.backgroundExecutor() instanceof ForkJoinPool)
		{
			activeThreads = ((ForkJoinPool)net.minecraft.Util.backgroundExecutor()).getActiveThreadCount();
		}
		final int execActiveThreads = activeThreads;

		ServerLevel overworld = this.nameToWorldMap.get(ResourceLocation.tryParse("minecraft:overworld"));
		this.nameToWorldMap.values().forEach(world -> {
			LevelLightEngine lightingProvider = world.getLightEngine();

			if (lightingProvider instanceof ILightScheduleDebug)
			{
				ILightScheduleDebug iProvider = (ILightScheduleDebug)lightingProvider;
				boolean isOverworld = world == overworld;
				iProvider.setBatchCsvEnabled$TISCM(this.isCsvSessionActive() && isOverworld);
				long runUpdate = iProvider.getRunUpdateCountAndClean$TISCM();
				long runUpdateMailbox = iProvider.getRunUpdateFromMailboxCountAndClean$TISCM();
				long runUpdateTrySched = iProvider.getRunUpdateFromTryScheduleCountAndClean$TISCM();
				long trySched = iProvider.getTryScheduleUpdateCountAndClean$TISCM();
				long runUpdateNanos = iProvider.getRunUpdateNanosAndClean$TISCM();
				long phaseA = iProvider.getPhaseANanosAndClean$TISCM();
				long phaseB = iProvider.getPhaseBNanosAndClean$TISCM();
				long phaseC = iProvider.getPhaseCNanosAndClean$TISCM();
				long lightTasksSizeSum = iProvider.getLightTasksSizeSumAndClean$TISCM();
				long lightMbxQueueSize = iProvider.getLightMailboxQueueSize$TISCM();

				if ((TISAdditionLoggerRegistry.__lightScheduleDebug || this.forceRecording) && isOverworld)
				{
					this.dataMap.get(world).add(new LightScheduleDebugRecordedData(
							runUpdate, runUpdateMailbox, runUpdateTrySched,
							trySched, lightMbx, sorterMbx, sorterPoll,
							runUpdateNanos, phaseA, phaseB, phaseC,
							blockEngNanos, skyEngNanos, lightUpdResult,
							blockNodesCheck, decreaseNodes, increaseNodes,
							blkChk, skyChk, blkDec, blkInc, skyDec, skyInc,
							secHit, secMiss, chunkTotal, chunkMiss,
							lightTasksSizeSum,
							lightMbxQueueSize, execActiveThreads
					));
				}
				else if (!isOverworld)
				{
					this.dataMap.get(world).clear();
				}
				else
				{
					this.dataMap.get(world).clear();
				}

				if (this.isCsvSessionActive() && world == overworld)
				{
					this.writeCsvLine(this.dataMap.get(world), lightMbxQueueSize, execActiveThreads);
				}
			}
		});
	}

	@Override
	public BaseComponent[] onHudUpdate(String option, Player playerEntity)
	{
		Level playerWorld = EntityUtils.getEntityWorld(playerEntity);
		if (!(playerWorld instanceof ServerLevel))
		{
			return new BaseComponent[]{Messenger.s("not ServerLevel")};
		}
		ResourceLocation optionId = ResourceLocation.tryParse(option);
		ServerLevel serverWorld = this.nameToWorldMap.getOrDefault(optionId, (ServerLevel)playerWorld);
		LightScheduleDebugWindowedDataRecorder recorder = this.dataMap.get(serverWorld);
		if (recorder == null)
		{
			return new BaseComponent[]{Messenger.s("no data")};
		}
		Deque<LightScheduleDebugRecordedData> deque = recorder.getQueue();
		if (deque.isEmpty())
		{
			return new BaseComponent[]{Messenger.s("no data")};
		}

		int size = deque.size();
		double runUpdate = (double)recorder.getRunUpdateCount() / size;
		double fromMailbox = (double)recorder.getRunUpdateFromMailbox() / size;
		double fromTrySched = (double)recorder.getRunUpdateFromTrySchedule() / size;
		double trySched = (double)recorder.getTryScheduleUpdateCount() / size;
		double lightMbx = (double)recorder.getLightMailboxRunCount() / size;
		double sorterMbx = (double)recorder.getSorterMailboxRunCount() / size;
		double sorterPoll = (double)recorder.getSorterPollTaskCount() / size;
		long totalRunUpdates = recorder.getRunUpdateCount();
		double avgRunUpdateMs = totalRunUpdates > 0
				? (double)recorder.getRunUpdateNanos() / totalRunUpdates / 1_000_000.0
				: 0.0;
		double avgPhaseAMs = totalRunUpdates > 0
				? (double)recorder.getPhaseANanos() / totalRunUpdates / 1_000_000.0
				: 0.0;
		double avgPhaseBMs = totalRunUpdates > 0
				? (double)recorder.getPhaseBNanos() / totalRunUpdates / 1_000_000.0
				: 0.0;
		double avgPhaseCMs = totalRunUpdates > 0
				? (double)recorder.getPhaseCNanos() / totalRunUpdates / 1_000_000.0
				: 0.0;
		double avgBlockEngMs = totalRunUpdates > 0
				? (double)recorder.getBlockEngineNanos() / totalRunUpdates / 1_000_000.0
				: 0.0;
		double avgSkyEngMs = totalRunUpdates > 0
				? (double)recorder.getSkyEngineNanos() / totalRunUpdates / 1_000_000.0
				: 0.0;
		double avgLightNodes = totalRunUpdates > 0
				? (double)recorder.getLightUpdatesResult() / totalRunUpdates
				: 0.0;
		double avgBlkChk = totalRunUpdates > 0
				? (double)recorder.getBlkChkSize() / totalRunUpdates
				: 0.0;
		double avgSkyChk = totalRunUpdates > 0
				? (double)recorder.getSkyChkSize() / totalRunUpdates
				: 0.0;
		double avgBlkDec = totalRunUpdates > 0
				? (double)recorder.getBlkDec() / totalRunUpdates
				: 0.0;
		double avgBlkInc = totalRunUpdates > 0
				? (double)recorder.getBlkInc() / totalRunUpdates
				: 0.0;
		double avgSkyDec = totalRunUpdates > 0
				? (double)recorder.getSkyDec() / totalRunUpdates
				: 0.0;
		double avgSkyInc = totalRunUpdates > 0
				? (double)recorder.getSkyInc() / totalRunUpdates
				: 0.0;
		double avgSecHit = totalRunUpdates > 0
				? (double)recorder.getSectionHit() / totalRunUpdates
				: 0.0;
		double avgSecMiss = totalRunUpdates > 0
				? (double)recorder.getSectionMiss() / totalRunUpdates
				: 0.0;
		double avgChunkTotal = totalRunUpdates > 0
				? (double)recorder.getChunkCacheTotal() / totalRunUpdates
				: 0.0;
		double avgChunkMiss = totalRunUpdates > 0
				? (double)recorder.getChunkCacheMiss() / totalRunUpdates
				: 0.0;
		double chunkHitRate = recorder.getChunkCacheTotal() > 0
				? 100.0 * (1.0 - (double)recorder.getChunkCacheMiss() / recorder.getChunkCacheTotal())
				: 0.0;
		double avgLightTasksSize = totalRunUpdates > 0
				? (double)recorder.getLightTasksSizeSum() / totalRunUpdates
				: 0.0;
		LightScheduleDebugRecordedData lastData = deque.getLast();
		long lightMbxQueueSize = lastData.lightMailboxQueueSize;
		int execActive = lastData.executorActiveThreads;

		BaseComponent header = Messenger.c(
				"g LSD(",
				Messenger.getColoredDimensionSymbol(DimensionWrapper.of(serverWorld)),
				"g ) "
		);

		return new BaseComponent[]{
				Messenger.c(
						header,
						"g runUpd: ",
						String.format("e %.1f", runUpdate),
						"g /gt (mbx:",
						String.format("c %.1f", fromMailbox),
						"g  trS:",
						String.format("c %.1f", fromTrySched),
						"g )"
				),
				Messenger.c(
						"g trySched: ",
						String.format("e %.1f", trySched),
						"g /gt  lightMbx: ",
						String.format("e %.1f", lightMbx),
						"g /gt"
				),
				Messenger.c(
						"g sorterMbx: ",
						String.format("e %.1f", sorterMbx),
						"g /gt  sorterPoll: ",
						String.format("e %.1f", sorterPoll),
						"g /gt"
				),
				Messenger.c(
						"g avgRunUpd: ",
						String.format("e %.2f", avgRunUpdateMs),
						"g ms  lMbxQ: ",
						String.format("e %d", lightMbxQueueSize),
						"g  execAct: ",
						String.format("e %d", execActive)
				),
				Messenger.c(
						"g  A(pre): ",
						String.format("e %.2f", avgPhaseAMs),
						"g ms  B(calc): ",
						String.format("e %.2f", avgPhaseBMs),
						"g ms  C(post+rm): ",
						String.format("e %.2f", avgPhaseCMs),
						"g ms"
				),
				Messenger.c(
						"g  blk: ",
						String.format("e %.2f", avgBlockEngMs),
						"g ms  sky: ",
						String.format("e %.2f", avgSkyEngMs),
						"g ms  nodes: ",
						String.format("e %.0f", avgLightNodes),
						"g /upd  qLen: ",
						String.format("e %.0f", avgLightTasksSize)
				),
				Messenger.c(
						"g  B chk:",
						String.format("e %.0f", avgBlkChk),
						"g  d:",
						String.format("e %.0f", avgBlkDec),
						"g  i:",
						String.format("e %.0f", avgBlkInc),
						"g  | S chk:",
						String.format("e %.0f", avgSkyChk),
						"g  d:",
						String.format("e %.0f", avgSkyDec),
						"g  i:",
						String.format("e %.0f", avgSkyInc)
				),
				Messenger.c(
						"g  secHit: ",
						String.format("e %.0f", avgSecHit),
						"g  secMiss: ",
						String.format("e %.0f", avgSecMiss),
						"g  chkHit%: ",
						String.format("e %.1f", chunkHitRate),
						"g  chkMiss: ",
						String.format("e %.0f", avgChunkMiss),
						"g /",
						String.format("e %.0f", avgChunkTotal)
				)
		};
	}
}
