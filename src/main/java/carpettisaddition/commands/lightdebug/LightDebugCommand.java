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

package carpettisaddition.commands.lightdebug;

import carpettisaddition.CarpetTISAdditionSettings;
import carpettisaddition.commands.AbstractCommand;
import carpettisaddition.commands.CommandTreeContext;
import carpettisaddition.logging.loggers.lightscheduledebug.LightScheduleDebugHUDLogger;
import carpettisaddition.utils.CarpetModUtil;
import carpettisaddition.utils.Messenger;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

import static com.mojang.brigadier.arguments.LongArgumentType.getLong;
import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class LightDebugCommand extends AbstractCommand
{
	private static final String NAME = "lightdebug";
	private static final LightDebugCommand INSTANCE = new LightDebugCommand();

	private LightDebugCommand()
	{
		super(NAME);
	}

	public static LightDebugCommand getInstance()
	{
		return INSTANCE;
	}

	@Override
	public void registerCommand(CommandTreeContext.Register context)
	{
		LiteralArgumentBuilder<CommandSourceStack> builder = literal(NAME).
				requires(source -> CarpetModUtil.canUseCommand(source, CarpetTISAdditionSettings.commandLightDebug)).
				then(argument("seconds", longArg(1L, Long.MAX_VALUE / 1000L)).
						executes(c -> start(c.getSource(), getLong(c, "seconds")))
				);
		context.dispatcher.register(builder);
	}

	private int start(CommandSourceStack source, long seconds)
	{
		LightScheduleDebugHUDLogger.getInstance().startCsvSession(seconds * 1000L);
		Messenger.tell(source, Messenger.s("Started light debug CSV recording for " + seconds + "s"));
		return 1;
	}
}
