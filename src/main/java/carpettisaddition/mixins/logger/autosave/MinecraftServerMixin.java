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

import carpettisaddition.logging.loggers.autosave.AutoSaveHUDLogger;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin
{
	//#if MC >= 12102
	//$$ @Inject(method = "autoSave", at = @At(value = "HEAD"))
	//#else
	@Inject(
			method = "tickServer",
			at = @At(
					value = "CONSTANT",
					args = "stringValue=save"
			)
	)
	//#endif
	private void onAutoSaveTriggered(CallbackInfo ci)
	{
		AutoSaveHUDLogger.getInstance().onAutoSaveTriggered();
	}

	//#if MC >= 12102
	//$$ @Inject(method = "autoSave", at = @At(value = "TAIL"))
	//#else
	@Inject(
			method = "tickServer",
			at = @At(
					value = "CONSTANT",
					args = "stringValue=Autosave finished"
			)
	)
	//#endif
	private void onAutoSaveCompleted(CallbackInfo ci)
	{
		AutoSaveHUDLogger.getInstance().onAutoSaveCompleted();
	}
}