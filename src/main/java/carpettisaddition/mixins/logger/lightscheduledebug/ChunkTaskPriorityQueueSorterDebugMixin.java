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

import carpettisaddition.logging.loggers.lightscheduledebug.LightScheduleDebugHUDLogger;
import net.minecraft.server.level.ChunkTaskPriorityQueue;
import net.minecraft.server.level.ChunkTaskPriorityQueueSorter;
import net.minecraft.util.thread.ProcessorHandle;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(ChunkTaskPriorityQueueSorter.class)
public abstract class ChunkTaskPriorityQueueSorterDebugMixin
{
	@Inject(method = "pollTask", at = @At("HEAD"))
	private <T> void onPollTask(ChunkTaskPriorityQueue<Function<ProcessorHandle<net.minecraft.util.Unit>, T>> queue, ProcessorHandle<T> processorHandle, CallbackInfo ci)
	{
		if (processorHandle.name().contains("light"))
		{
			LightScheduleDebugHUDLogger.getInstance().incrementSorterPollTask();
		}
	}
}
