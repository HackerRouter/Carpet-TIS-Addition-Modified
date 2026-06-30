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

public class LightScheduleDebugRecordedData
{
	public final long runUpdateCount;
	public final long runUpdateFromMailbox;
	public final long runUpdateFromTrySchedule;
	public final long tryScheduleUpdateCount;
	public final long lightMailboxRunCount;
	public final long sorterMailboxRunCount;
	public final long sorterPollTaskCount;
	public final long runUpdateNanos;
	public final long phaseANanos;
	public final long phaseBNanos;
	public final long phaseCNanos;
	public final long blockEngineNanos;
	public final long skyEngineNanos;
	public final long lightUpdatesResult;
	public final long blockNodesToCheckSize;
	public final long decreaseNodes;
	public final long increaseNodes;
	public final long blkChkSize;
	public final long skyChkSize;
	public final long blkDec;
	public final long blkInc;
	public final long skyDec;
	public final long skyInc;
	public final long sectionHit;
	public final long sectionMiss;
	public final long chunkCacheTotal;
	public final long chunkCacheMiss;
	public final long lightTasksSizeSum;
	public final long lightMailboxQueueSize;
	public final int executorActiveThreads;

	public LightScheduleDebugRecordedData(
			long runUpdateCount,
			long runUpdateFromMailbox,
			long runUpdateFromTrySchedule,
			long tryScheduleUpdateCount,
			long lightMailboxRunCount,
			long sorterMailboxRunCount,
			long sorterPollTaskCount,
			long runUpdateNanos,
			long phaseANanos,
			long phaseBNanos,
			long phaseCNanos,
			long blockEngineNanos,
			long skyEngineNanos,
			long lightUpdatesResult,
			long blockNodesToCheckSize,
			long decreaseNodes,
			long increaseNodes,
			long blkChkSize,
			long skyChkSize,
			long blkDec,
			long blkInc,
			long skyDec,
			long skyInc,
			long sectionHit,
			long sectionMiss,
			long chunkCacheTotal,
			long chunkCacheMiss,
			long lightTasksSizeSum,
			long lightMailboxQueueSize,
			int executorActiveThreads
	)
	{
		this.runUpdateCount = runUpdateCount;
		this.runUpdateFromMailbox = runUpdateFromMailbox;
		this.runUpdateFromTrySchedule = runUpdateFromTrySchedule;
		this.tryScheduleUpdateCount = tryScheduleUpdateCount;
		this.lightMailboxRunCount = lightMailboxRunCount;
		this.sorterMailboxRunCount = sorterMailboxRunCount;
		this.sorterPollTaskCount = sorterPollTaskCount;
		this.runUpdateNanos = runUpdateNanos;
		this.phaseANanos = phaseANanos;
		this.phaseBNanos = phaseBNanos;
		this.phaseCNanos = phaseCNanos;
		this.blockEngineNanos = blockEngineNanos;
		this.skyEngineNanos = skyEngineNanos;
		this.lightUpdatesResult = lightUpdatesResult;
		this.blockNodesToCheckSize = blockNodesToCheckSize;
		this.decreaseNodes = decreaseNodes;
		this.increaseNodes = increaseNodes;
		this.blkChkSize = blkChkSize;
		this.skyChkSize = skyChkSize;
		this.blkDec = blkDec;
		this.blkInc = blkInc;
		this.skyDec = skyDec;
		this.skyInc = skyInc;
		this.sectionHit = sectionHit;
		this.sectionMiss = sectionMiss;
		this.chunkCacheTotal = chunkCacheTotal;
		this.chunkCacheMiss = chunkCacheMiss;
		this.lightTasksSizeSum = lightTasksSizeSum;
		this.lightMailboxQueueSize = lightMailboxQueueSize;
		this.executorActiveThreads = executorActiveThreads;
	}
}
