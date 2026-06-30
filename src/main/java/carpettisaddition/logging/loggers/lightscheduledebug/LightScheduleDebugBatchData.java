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

public class LightScheduleDebugBatchData
{
	public final long runUpdateId;
	public final int batchSize;
	public final int uniqueChunks;
	public final int maxChunkTasks;
	public final String[] topChunks;
	public final int[] topCounts;
	public final int[] levelCounts;
	public final int otherLevelCount;

	public LightScheduleDebugBatchData(long runUpdateId, int batchSize, int uniqueChunks, int maxChunkTasks, String[] topChunks, int[] topCounts, int[] levelCounts, int otherLevelCount)
	{
		this.runUpdateId = runUpdateId;
		this.batchSize = batchSize;
		this.uniqueChunks = uniqueChunks;
		this.maxChunkTasks = maxChunkTasks;
		this.topChunks = topChunks;
		this.topCounts = topCounts;
		this.levelCounts = levelCounts;
		this.otherLevelCount = otherLevelCount;
	}
}
