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

package carpettisaddition.logging.loggers.autosave;

import carpettisaddition.logging.TISAdditionLoggerRegistry;
import carpettisaddition.logging.loggers.AbstractHUDLogger;
import carpettisaddition.utils.EntityUtils;
import carpettisaddition.utils.Messenger;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Arrays;

//#if MC >= 11802
//$$ import carpettisaddition.utils.PositionUtils;
//$$ import carpettisaddition.utils.compat.DimensionWrapper;
//#endif

public class AutoSaveHUDLogger extends AbstractHUDLogger
{
	public static final String NAME = "autoSave";
	private static final AutoSaveHUDLogger INSTANCE = new AutoSaveHUDLogger();

	// The autosave interval in ticks (vanilla default)
	private static final int AUTOSAVE_INTERVAL = 6000;

	private AutoSaveHUDLogger()
	{
		super(NAME, true);
	}

	public static AutoSaveHUDLogger getInstance()
	{
		return INSTANCE;
	}

	@Override
	public String getDefaultLoggingOption()
	{
		return LoggingType.COUNTDOWN.getName();
	}

	@Override
	public String[] getSuggestedLoggingOption()
	{
		return LoggingType.LOGGING_SUGGESTIONS;
	}

	@Override
	public BaseComponent[] onHudUpdate(String option, Player playerEntity)
	{
		if (!LoggingType.COUNTDOWN.isContainedIn(option))
		{
			return null;
		}

		Level playerWorld = EntityUtils.getEntityWorld(playerEntity);
		if (!(playerWorld instanceof ServerLevel))
		{
			return null;
		}
		MinecraftServer server = ((ServerLevel)playerWorld).getServer();
		int tickCount = server.getTickCount();
		int remaining = AUTOSAVE_INTERVAL - (tickCount % AUTOSAVE_INTERVAL);

		ChatFormatting color;
		if (remaining <= 200)  // <= 10 seconds
		{
			color = ChatFormatting.RED;
		}
		else if (remaining <= 1200)  // <= 1 minute
		{
			color = ChatFormatting.GOLD;
		}
		else
		{
			color = ChatFormatting.GREEN;
		}

		return new BaseComponent[]{Messenger.formatting(
				Messenger.c(
						Messenger.s("AutoSave "),
						Messenger.s(remaining + "gt", color),
						Messenger.s(" / " + AUTOSAVE_INTERVAL + "gt", ChatFormatting.DARK_GRAY)
				),
				ChatFormatting.GRAY
		)};
	}

	/**
	 * Called from mixin when autosave is triggered
	 */
	public void onAutoSaveTriggered()
	{
		if (!TISAdditionLoggerRegistry.__autoSave)
		{
			return;
		}
		this.logToChat(option -> {
			if (!LoggingType.NOTIFICATION.isContainedIn(option))
			{
				return null;
			}
			return new BaseComponent[]{
					Messenger.formatting(
							tr("triggered"),
							ChatFormatting.YELLOW
					)
			};
		});
	}

	/**
	 * Called from mixin when autosave finishes
	 */
	public void onAutoSaveCompleted()
	{
		if (!TISAdditionLoggerRegistry.__autoSave)
		{
			return;
		}
		this.logToChat(option -> {
			if (!LoggingType.NOTIFICATION.isContainedIn(option))
			{
				return null;
			}
			return new BaseComponent[]{
					Messenger.formatting(
							tr("completed"),
							ChatFormatting.GREEN
					)
			};
		});
	}

	//#if MC >= 11802
	//$$ /**
	//$$ * Called from ChunkMap mixin when a chunk save attempt completes
	//$$ * @param chunkPos the chunk position
	//$$ * @param level the server level
	//$$ * @param saved true if the chunk was actually saved, false if skipped (cooldown or not dirty)
	//$$ */
	//$$ public void onChunkSaveAttempt(ChunkPos chunkPos, ServerLevel level, boolean saved)
	//$$ {
	//$$ 	if (!TISAdditionLoggerRegistry.__autoSave || !saved)
	//$$ 	{
	//$$ 		return;
	//$$ 	}
	//$$ 	this.logToChat(option -> {
	//$$ 		if (!LoggingType.CHUNK_SAVE.isContainedIn(option))
	//$$ 		{
	//$$ 			return null;
	//$$ 		}
	//$$ 		return new BaseComponent[]{
	//$$ 				Messenger.formatting(
	//$$ 						Messenger.c(
	//$$ 								tr("chunk_saved",
	//$$ 										PositionUtils.chunkPosX(chunkPos), PositionUtils.chunkPosZ(chunkPos),
	//$$ 										Messenger.dimension(DimensionWrapper.of(level))
	//$$ 								)
	//$$ 						),
	//$$ 						ChatFormatting.AQUA
	//$$ 				)
	//$$ 		};
	//$$ 	});
	//$$ }
	//#endif

	private enum LoggingType
	{
		COUNTDOWN,
		NOTIFICATION,
		CHUNK_SAVE,
		ALL;

		public static final String[] LOGGING_SUGGESTIONS;

		public String getName()
		{
			return this.name().toLowerCase();
		}

		@SuppressWarnings("BooleanMethodIsAlwaysInverted")
		public boolean isContainedIn(String option)
		{
			return ALL.getName().equals(option) || Arrays.asList(option.split(MULTI_OPTION_SEP_REG)).contains(this.getName());
		}

		static
		{
			LOGGING_SUGGESTIONS = Arrays.stream(values()).map(LoggingType::getName).toArray(String[]::new);
		}
	}
}