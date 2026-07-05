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

package carpettisaddition.mixins.command.manipulate.chunk;

import carpettisaddition.utils.ModIds;
import carpettisaddition.utils.compat.DummyClass;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//#if MC >= 11802
//$$ import net.minecraft.server.level.ChunkMap;
//#endif

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.18.2"))
//#if MC >= 11802
//$$ @Mixin(ChunkMap.class)
//#else
@Mixin(DummyClass.class)
//#endif
public interface ChunkMapSaveCooldownAccessor
{
	//#if MC >= 12103
	//$$ @Accessor("nextChunkSaveTime")
	//$$ Long2LongMap getChunkSaveCooldowns$TISCM();
	//#elseif MC >= 11802
	//$$ @Accessor("chunkSaveCooldowns")
	//$$ Long2LongMap getChunkSaveCooldowns$TISCM();
	//#endif
}