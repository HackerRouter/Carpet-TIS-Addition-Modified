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
import net.minecraft.util.thread.ProcessorMailbox;
import net.minecraft.util.thread.StrictQueue;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProcessorMailbox.class)
public abstract class ProcessorMailboxDebugMixin<T>
{
	@Shadow @Final private String name;

	@Inject(method = "run", at = @At("HEAD"))
	private void onRun(CallbackInfo ci)
	{
		if ("light".equals(this.name))
		{
			LightScheduleDebugHUDLogger.getInstance().incrementLightMailboxRun();
		}
		else if ("sorter".equals(this.name))
		{
			LightScheduleDebugHUDLogger.getInstance().incrementSorterMailboxRun();
		}
	}
}
