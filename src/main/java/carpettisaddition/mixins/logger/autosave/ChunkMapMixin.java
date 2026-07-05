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

package carpettisaddition.mixins.logger.autosave;

//#if MC >= 11802

//$$ import carpettisaddition.logging.loggers.autosave.AutoSaveHUDLogger;
//$$ import net.minecraft.server.level.ChunkHolder;
//$$ import net.minecraft.server.level.ChunkMap;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.world.level.ChunkPos;
//$$ import org.spongepowered.asm.mixin.Final;
//$$ import org.spongepowered.asm.mixin.Mixin;
//$$ import org.spongepowered.asm.mixin.Shadow;
//$$ import org.spongepowered.asm.mixin.injection.At;
//$$ import org.spongepowered.asm.mixin.injection.Inject;
//$$ import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#else
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.server.level.ChunkMap;
//#endif

@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin
{
	//#if MC >= 11802

	//$$ @Shadow @Final
	//$$ ServerLevel level;

	//#if MC >= 12103
	//$$ @Inject(method = "saveChunkIfNeeded", at = @At("RETURN"))
	//$$ private void onSaveChunkIfNeeded(ChunkHolder chunkHolder, long l, CallbackInfoReturnable<Boolean> cir)
	//#else
	//$$ @Inject(method = "saveChunkIfNeeded", at = @At("RETURN"))
	//$$ private void onSaveChunkIfNeeded(ChunkHolder chunkHolder, CallbackInfoReturnable<Boolean> cir)
	//#endif
	//$$ {
	//$$ 	AutoSaveHUDLogger.getInstance().onChunkSaveAttempt(
	//$$ 			chunkHolder.getPos(),
	//$$ 			this.level,
	//$$ 			cir.getReturnValue()
	//$$ 	);
	//$$ }

	//#endif
}