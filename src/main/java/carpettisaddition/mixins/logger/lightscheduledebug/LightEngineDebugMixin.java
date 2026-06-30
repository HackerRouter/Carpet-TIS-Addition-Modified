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
//$$ import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
//$$ import net.minecraft.world.level.lighting.BlockLightEngine;
//$$ import net.minecraft.world.level.lighting.LightEngine;
//$$ import net.minecraft.world.level.chunk.LightChunk;
//$$ import net.minecraft.world.level.chunk.LightChunkGetter;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.Unique;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.Redirect;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//#endif

//#if MC >= 12004
//$$ @Mixin(LightEngine.class)
//$$ public abstract class LightEngineDebugMixin
//$$ {
//$$	@Shadow @Final private LongOpenHashSet blockNodesToCheck;
//$$	@Unique private int decreaseNodes;
//$$
//$$	@Unique
//$$	private boolean isBlockEngine()
//$$	{
//$$		return (Object)this instanceof BlockLightEngine;
//$$	}
//$$
//$$	@Inject(method = "runLightUpdates", at = @At("HEAD"))
//$$	private void onRunLightUpdatesHead(CallbackInfoReturnable<Integer> cir)
//$$	{
//$$		if (this.isBlockEngine())
//$$		{
//$$			LightScheduleDebugHUDLogger.getInstance().addBlkChkSize(this.blockNodesToCheck.size());
//$$		}
//$$		else
//$$		{
//$$			LightScheduleDebugHUDLogger.getInstance().addSkyChkSize(this.blockNodesToCheck.size());
//$$		}
//$$	}
//$$
//$$	@Inject(method = "propagateDecreases", at = @At("RETURN"))
//$$	private void onPropagateDecreasesReturn(CallbackInfoReturnable<Integer> cir)
//$$	{
//$$		this.decreaseNodes = cir.getReturnValue();
//$$	}
//$$
//$$	@Inject(method = "propagateIncreases", at = @At("RETURN"))
//$$	private void onPropagateIncreasesReturn(CallbackInfoReturnable<Integer> cir)
//$$	{
//$$		int increaseNodes = cir.getReturnValue();
//$$		if (this.isBlockEngine())
//$$		{
//$$			LightScheduleDebugHUDLogger.getInstance().addBlkDec(this.decreaseNodes);
//$$			LightScheduleDebugHUDLogger.getInstance().addBlkInc(increaseNodes);
//$$		}
//$$		else
//$$		{
//$$			LightScheduleDebugHUDLogger.getInstance().addSkyDec(this.decreaseNodes);
//$$			LightScheduleDebugHUDLogger.getInstance().addSkyInc(increaseNodes);
//$$		}
//$$	}
//$$
//$$	@Inject(method = "getChunk", at = @At("HEAD"))
//$$	private void onGetChunkHead(int i, int j, CallbackInfoReturnable<LightChunk> cir)
//$$	{
//$$		if (this.isBlockEngine())
//$$		{
//$$			LightScheduleDebugHUDLogger.getInstance().incrementChunkCacheTotal();
//$$		}
//$$	}
//$$
//$$	@Redirect(
//$$			method = "getChunk",
//$$			at = @At(
//$$					value = "INVOKE",
//$$					target = "Lnet/minecraft/world/level/chunk/LightChunkGetter;getChunkForLighting(II)Lnet/minecraft/world/level/chunk/LightChunk;"
//$$			)
//$$	)
//$$	private LightChunk trackChunkCacheMiss(LightChunkGetter getter, int x, int z)
//$$	{
//$$		if (this.isBlockEngine())
//$$		{
//$$			LightScheduleDebugHUDLogger.getInstance().incrementChunkCacheMiss();
//$$		}
//$$		return getter.getChunkForLighting(x, z);
//$$	}
//$$ }
//#else
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.world.level.lighting.LevelLightEngine;

@Mixin(LevelLightEngine.class)
public abstract class LightEngineDebugMixin
{
}
//#endif
