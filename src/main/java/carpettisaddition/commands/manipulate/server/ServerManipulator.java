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

package carpettisaddition.commands.manipulate.server;

import carpettisaddition.commands.CommandTreeContext;
import carpettisaddition.commands.manipulate.AbstractManipulator;
import carpettisaddition.logging.loggers.entityIdCounter.EntityIdCounterUtils;
import carpettisaddition.mixins.command.info.server.EntityAccessor;
import carpettisaddition.mixins.command.manipulate.server.MinecraftServerAccessor;
import carpettisaddition.utils.Messenger;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.world.entity.Entity;

import java.util.concurrent.atomic.AtomicInteger;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.arguments.EntityArgument.entity;
import static net.minecraft.commands.arguments.EntityArgument.getEntity;

public class ServerManipulator extends AbstractManipulator
{
	public ServerManipulator()
	{
		super("server");
	}

	@Override
	public void buildSubCommand(CommandTreeContext.Node context)
	{
		context.node.then(literal("entity_id_counter").
				executes(c -> showEntityIdCounter(c.getSource())).
				then(literal("show").
						executes(c -> showEntityIdCounter(c.getSource()))
				).
				then(literal("query").
						then(argument("entity", entity()).
								executes(c -> queryEntityIdCounter(c.getSource(), getEntity(c, "entity")))
						)
				).
				then(literal("set").
						then(argument("value", integer()).
								executes(c -> setEntityIdCounter(c.getSource(), getInteger(c, "value")))
						)
				).
				then(literal("add").
						then(argument("delta", integer()).
								executes(c -> addEntityIdCounter(c.getSource(), getInteger(c, "delta")))
						)
				)
		);

		context.node.then(literal("tick_count").
				executes(c -> showTickCount(c.getSource())).
				then(literal("show").
						executes(c -> showTickCount(c.getSource()))
				).
				then(literal("set").
						then(argument("value", integer(Integer.MIN_VALUE, Integer.MAX_VALUE)).
								executes(c -> setTickCount(c.getSource(), getInteger(c, "value")))
						)
				).
				then(literal("add").
						then(argument("delta", integer()).
								executes(c -> addTickCount(c.getSource(), getInteger(c, "delta")))
						)
				)
		);
	}

	private static AtomicInteger getEntityIdCounter()
	{
		return EntityAccessor.getEntityIdCounter$TISCM();
	}

	private BaseComponent prettyEidCounter(int value)
	{
		double overflowTo0Percent = EntityIdCounterUtils.getPercentOfOverflowToZero(value);
		double overflowToNegativePercent = EntityIdCounterUtils.getPercentOfOverflowToNegative(value);
		return Messenger.hover(
				Messenger.s(value),
				Messenger.c(
						tr("entity_id_counter.progress_of_overflow_to_negative", String.format("%.4f%%", overflowToNegativePercent)),
						Messenger.newLine(),
						tr("entity_id_counter.progress_of_overflow_to_0", String.format("%.4f%%", overflowTo0Percent))
				)
		);
	}

	private int addEntityIdCounter(CommandSourceStack source, int delta)
	{
		int newValue = getEntityIdCounter().addAndGet(delta);
		Messenger.tell(source, tr("entity_id_counter.add", delta, this.prettyEidCounter(newValue)));
		return 0;
	}

	private int setEntityIdCounter(CommandSourceStack source, int value)
	{
		getEntityIdCounter().set(value);
		Messenger.tell(source, tr("entity_id_counter.set", this.prettyEidCounter(value)));
		return 0;
	}

	private int showEntityIdCounter(CommandSourceStack source)
	{
		int value = getEntityIdCounter().get();
		Messenger.tell(source, tr("entity_id_counter.show", this.prettyEidCounter(value)));
		return 0;
	}

	private int queryEntityIdCounter(CommandSourceStack source, Entity entity)
	{
		int value = entity.getId();
		Messenger.tell(source, tr("entity_id_counter.query", Messenger.entity(entity), this.prettyEidCounter(value)));
		return 0;
	}

	private static MinecraftServerAccessor getServerAccessor(CommandSourceStack source)
	{
		return (MinecraftServerAccessor) source.getServer();
	}

	private BaseComponent prettyTickCount(int value)
	{
		double overflowToNegativePercent = ((double) value - Integer.MIN_VALUE) / ((double) Integer.MAX_VALUE - Integer.MIN_VALUE) * 100.0;
		double overflowTo0Percent = ((double) value + Integer.MAX_VALUE + 1) / ((double) Integer.MAX_VALUE - Integer.MIN_VALUE + 1) * 100.0;
		return Messenger.hover(
				Messenger.s(value),
				Messenger.c(
						tr("tick_count.progress_of_overflow_to_negative", String.format("%.4f%%", overflowToNegativePercent)),
						Messenger.newLine(),
						tr("tick_count.progress_of_overflow_to_0", String.format("%.4f%%", overflowTo0Percent))
				)
		);
	}

	private int showTickCount(CommandSourceStack source)
	{
		int value = getServerAccessor(source).getTickCount$TISCM();
		Messenger.tell(source, tr("tick_count.show", this.prettyTickCount(value)));
		return 0;
	}

	private int setTickCount(CommandSourceStack source, int value)
	{
		getServerAccessor(source).setTickCount$TISCM(value);
		Messenger.tell(source, tr("tick_count.set", this.prettyTickCount(value)));
		return 0;
	}

	private int addTickCount(CommandSourceStack source, int delta)
	{
		MinecraftServerAccessor server = getServerAccessor(source);
		int oldValue = server.getTickCount$TISCM();
		int newValue = oldValue + delta;
		server.setTickCount$TISCM(newValue);
		Messenger.tell(source, tr("tick_count.add", delta, this.prettyTickCount(newValue)));
		return 0;
	}
}
