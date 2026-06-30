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

//#if MC >= 12004
//$$ import carpettisaddition.logging.loggers.lightscheduledebug.LightScheduleDebugHUDLogger;
//$$ import net.minecraft.world.level.lighting.BlockLightEngine;
//$$ import net.minecraft.world.level.lighting.BlockLightSectionStorage;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//#endif

//#if MC >= 12004
//$$ @Mixin(BlockLightEngine.class)
//$$ public abstract class BlockLightEngineDebugMixin
//$$ {
//$$	@Redirect(
//$$			method = "propagateIncrease",
//$$			at = @At(
//$$					value = "INVOKE",
//$$					target = "Lnet/minecraft/world/level/lighting/BlockLightSectionStorage;storingLightForSection(J)Z"
//$$			)
//$$	)
//$$	private boolean trackStoringInIncrease(BlockLightSectionStorage storage, long sectionPos)
//$$	{
//$$		boolean result = ((LayerLightSectionStorageAccessor)storage).invokeStoringLightForSection(sectionPos);
//$$		if (result)
//$$		{
//$$			LightScheduleDebugHUDLogger.getInstance().incrementSectionHit();
//$$		}
//$$		else
//$$		{
//$$			LightScheduleDebugHUDLogger.getInstance().incrementSectionMiss();
//$$		}
//$$		return result;
//$$	}
//$$
//$$	@Redirect(
//$$			method = "propagateDecrease",
//$$			at = @At(
//$$					value = "INVOKE",
//$$					target = "Lnet/minecraft/world/level/lighting/BlockLightSectionStorage;storingLightForSection(J)Z"
//$$			)
//$$	)
//$$	private boolean trackStoringInDecrease(BlockLightSectionStorage storage, long sectionPos)
//$$	{
//$$		boolean result = ((LayerLightSectionStorageAccessor)storage).invokeStoringLightForSection(sectionPos);
//$$		if (result)
//$$		{
//$$			LightScheduleDebugHUDLogger.getInstance().incrementSectionHit();
//$$		}
//$$		else
//$$		{
//$$			LightScheduleDebugHUDLogger.getInstance().incrementSectionMiss();
//$$		}
//$$		return result;
//$$	}
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.level.lighting.LevelLightEngine;

@Mixin(LevelLightEngine.class)
public abstract class BlockLightEngineDebugMixin
{
}
//#endif
