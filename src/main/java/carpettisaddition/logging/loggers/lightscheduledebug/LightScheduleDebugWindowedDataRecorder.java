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

import carpettisaddition.CarpetTISAdditionSettings;
import com.google.common.collect.Queues;

import java.util.Deque;

public class LightScheduleDebugWindowedDataRecorder
{
	private final Deque<LightScheduleDebugRecordedData> dataQueue = Queues.newArrayDeque();
	private long runUpdateCount = 0;
	private long runUpdateFromMailbox = 0;
	private long runUpdateFromTrySchedule = 0;
	private long tryScheduleUpdateCount = 0;
	private long lightMailboxRunCount = 0;
	private long sorterMailboxRunCount = 0;
	private long sorterPollTaskCount = 0;
	private long runUpdateNanos = 0;
	private long phaseANanos = 0;
	private long phaseBNanos = 0;
	private long phaseCNanos = 0;
	private long blockEngineNanos = 0;
	private long skyEngineNanos = 0;
	private long lightUpdatesResult = 0;
	private long blockNodesToCheckSize = 0;
	private long decreaseNodes = 0;
	private long increaseNodes = 0;
	private long blkChkSize = 0;
	private long skyChkSize = 0;
	private long blkDec = 0;
	private long blkInc = 0;
	private long skyDec = 0;
	private long skyInc = 0;
	private long sectionHit = 0;
	private long sectionMiss = 0;
	private long chunkCacheTotal = 0;
	private long chunkCacheMiss = 0;
	private long lightTasksSizeSum = 0;

	public void add(LightScheduleDebugRecordedData newData)
	{
		this.runUpdateCount += newData.runUpdateCount;
		this.runUpdateFromMailbox += newData.runUpdateFromMailbox;
		this.runUpdateFromTrySchedule += newData.runUpdateFromTrySchedule;
		this.tryScheduleUpdateCount += newData.tryScheduleUpdateCount;
		this.lightMailboxRunCount += newData.lightMailboxRunCount;
		this.sorterMailboxRunCount += newData.sorterMailboxRunCount;
		this.sorterPollTaskCount += newData.sorterPollTaskCount;
		this.runUpdateNanos += newData.runUpdateNanos;
		this.phaseANanos += newData.phaseANanos;
		this.phaseBNanos += newData.phaseBNanos;
		this.phaseCNanos += newData.phaseCNanos;
		this.blockEngineNanos += newData.blockEngineNanos;
		this.skyEngineNanos += newData.skyEngineNanos;
		this.lightUpdatesResult += newData.lightUpdatesResult;
		this.blockNodesToCheckSize += newData.blockNodesToCheckSize;
		this.decreaseNodes += newData.decreaseNodes;
		this.increaseNodes += newData.increaseNodes;
		this.blkChkSize += newData.blkChkSize;
		this.skyChkSize += newData.skyChkSize;
		this.blkDec += newData.blkDec;
		this.blkInc += newData.blkInc;
		this.skyDec += newData.skyDec;
		this.skyInc += newData.skyInc;
		this.sectionHit += newData.sectionHit;
		this.sectionMiss += newData.sectionMiss;
		this.chunkCacheTotal += newData.chunkCacheTotal;
		this.chunkCacheMiss += newData.chunkCacheMiss;
		this.lightTasksSizeSum += newData.lightTasksSizeSum;
		this.dataQueue.addLast(newData);
		while (this.dataQueue.size() > CarpetTISAdditionSettings.lightQueueLoggerSamplingDuration)
		{
			LightScheduleDebugRecordedData data = this.dataQueue.removeFirst();
			this.runUpdateCount -= data.runUpdateCount;
			this.runUpdateFromMailbox -= data.runUpdateFromMailbox;
			this.runUpdateFromTrySchedule -= data.runUpdateFromTrySchedule;
			this.tryScheduleUpdateCount -= data.tryScheduleUpdateCount;
			this.lightMailboxRunCount -= data.lightMailboxRunCount;
			this.sorterMailboxRunCount -= data.sorterMailboxRunCount;
			this.sorterPollTaskCount -= data.sorterPollTaskCount;
			this.runUpdateNanos -= data.runUpdateNanos;
			this.phaseANanos -= data.phaseANanos;
			this.phaseBNanos -= data.phaseBNanos;
			this.phaseCNanos -= data.phaseCNanos;
			this.blockEngineNanos -= data.blockEngineNanos;
			this.skyEngineNanos -= data.skyEngineNanos;
			this.lightUpdatesResult -= data.lightUpdatesResult;
			this.blockNodesToCheckSize -= data.blockNodesToCheckSize;
			this.decreaseNodes -= data.decreaseNodes;
			this.increaseNodes -= data.increaseNodes;
			this.blkChkSize -= data.blkChkSize;
			this.skyChkSize -= data.skyChkSize;
			this.blkDec -= data.blkDec;
			this.blkInc -= data.blkInc;
			this.skyDec -= data.skyDec;
			this.skyInc -= data.skyInc;
			this.sectionHit -= data.sectionHit;
			this.sectionMiss -= data.sectionMiss;
			this.chunkCacheTotal -= data.chunkCacheTotal;
			this.chunkCacheMiss -= data.chunkCacheMiss;
			this.lightTasksSizeSum -= data.lightTasksSizeSum;
		}
	}

	public Deque<LightScheduleDebugRecordedData> getQueue()
	{
		return this.dataQueue;
	}

	public long getRunUpdateCount() { return this.runUpdateCount; }
	public long getRunUpdateFromMailbox() { return this.runUpdateFromMailbox; }
	public long getRunUpdateFromTrySchedule() { return this.runUpdateFromTrySchedule; }
	public long getTryScheduleUpdateCount() { return this.tryScheduleUpdateCount; }
	public long getLightMailboxRunCount() { return this.lightMailboxRunCount; }
	public long getSorterMailboxRunCount() { return this.sorterMailboxRunCount; }
	public long getSorterPollTaskCount() { return this.sorterPollTaskCount; }
	public long getRunUpdateNanos() { return this.runUpdateNanos; }
	public long getPhaseANanos() { return this.phaseANanos; }
	public long getPhaseBNanos() { return this.phaseBNanos; }
	public long getPhaseCNanos() { return this.phaseCNanos; }
	public long getBlockEngineNanos() { return this.blockEngineNanos; }
	public long getSkyEngineNanos() { return this.skyEngineNanos; }
	public long getLightUpdatesResult() { return this.lightUpdatesResult; }
	public long getBlockNodesToCheckSize() { return this.blockNodesToCheckSize; }
	public long getDecreaseNodes() { return this.decreaseNodes; }
	public long getIncreaseNodes() { return this.increaseNodes; }
	public long getBlkChkSize() { return this.blkChkSize; }
	public long getSkyChkSize() { return this.skyChkSize; }
	public long getBlkDec() { return this.blkDec; }
	public long getBlkInc() { return this.blkInc; }
	public long getSkyDec() { return this.skyDec; }
	public long getSkyInc() { return this.skyInc; }
	public long getSectionHit() { return this.sectionHit; }
	public long getSectionMiss() { return this.sectionMiss; }
	public long getChunkCacheTotal() { return this.chunkCacheTotal; }
	public long getChunkCacheMiss() { return this.chunkCacheMiss; }
	public long getLightTasksSizeSum() { return this.lightTasksSizeSum; }

	public void clear()
	{
		this.dataQueue.clear();
		this.runUpdateCount = 0;
		this.runUpdateFromMailbox = 0;
		this.runUpdateFromTrySchedule = 0;
		this.tryScheduleUpdateCount = 0;
		this.lightMailboxRunCount = 0;
		this.sorterMailboxRunCount = 0;
		this.sorterPollTaskCount = 0;
		this.runUpdateNanos = 0;
		this.phaseANanos = 0;
		this.phaseBNanos = 0;
		this.phaseCNanos = 0;
		this.blockEngineNanos = 0;
		this.skyEngineNanos = 0;
		this.lightUpdatesResult = 0;
		this.blockNodesToCheckSize = 0;
		this.decreaseNodes = 0;
		this.increaseNodes = 0;
		this.blkChkSize = 0;
		this.skyChkSize = 0;
		this.blkDec = 0;
		this.blkInc = 0;
		this.skyDec = 0;
		this.skyInc = 0;
		this.sectionHit = 0;
		this.sectionMiss = 0;
		this.chunkCacheTotal = 0;
		this.chunkCacheMiss = 0;
		this.lightTasksSizeSum = 0;
	}
}
