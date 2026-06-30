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
//$$ import net.minecraft.world.level.lighting.LevelLightEngine;
//$$ import net.minecraft.world.level.lighting.LightEngine;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#endif

//#if MC >= 12004
//$$ @Mixin(LevelLightEngine.class)
//$$ public abstract class LevelLightEngineDebugMixin
//$$ {
//$$	@Unique private long blockEngineStartNano;
//$$	@Unique private long skyEngineStartNano;
//$$
//$$	@Inject(
//$$			method = "runLightUpdates",
//$$			at = @At(
//$$					value = "INVOKE",
//$$					target = "Lnet/minecraft/world/level/lighting/LightEngine;runLightUpdates()I",
//$$					ordinal = 0
//$$			)
//$$	)
//$$	private void beforeBlockEngine(CallbackInfoReturnable<Integer> cir)
//$$	{
//$$		this.blockEngineStartNano = System.nanoTime();
//$$	}
//$$
//$$	@Inject(
//$$			method = "runLightUpdates",
//$$			at = @At(
//$$					value = "INVOKE",
//$$					target = "Lnet/minecraft/world/level/lighting/LightEngine;runLightUpdates()I",
//$$					ordinal = 0,
//$$					shift = At.Shift.AFTER
//$$			)
//$$	)
//$$	private void afterBlockEngine(CallbackInfoReturnable<Integer> cir)
//$$	{
//$$		long now = System.nanoTime();
//$$		long blockNanos = now - this.blockEngineStartNano;
//$$		this.skyEngineStartNano = now;
//$$		LightScheduleDebugHUDLogger.getInstance().addBlockEngineNanos(blockNanos);
//$$	}
//$$
//$$	@Inject(
//$$			method = "runLightUpdates",
//$$			at = @At(
//$$					value = "INVOKE",
//$$					target = "Lnet/minecraft/world/level/lighting/LightEngine;runLightUpdates()I",
//$$					ordinal = 1,
//$$					shift = At.Shift.AFTER
//$$			)
//$$	)
//$$	private void afterSkyEngine(CallbackInfoReturnable<Integer> cir)
//$$	{
//$$		long skyNanos = System.nanoTime() - this.skyEngineStartNano;
//$$		LightScheduleDebugHUDLogger.getInstance().addSkyEngineNanos(skyNanos);
//$$	}
//$$
//$$	@Inject(method = "runLightUpdates", at = @At("RETURN"))
//$$	private void onReturn(CallbackInfoReturnable<Integer> cir)
//$$	{
//$$		LightScheduleDebugHUDLogger.getInstance().addRunLightUpdatesResult(cir.getReturnValue());
//$$	}
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.level.lighting.LevelLightEngine;

@Mixin(LevelLightEngine.class)
public abstract class LevelLightEngineDebugMixin
{
}
//#endif
