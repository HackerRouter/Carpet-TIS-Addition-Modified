/*
 * This file is part of the Carpet TIS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package carpettisaddition.commands.manipulate.chunk;

import carpettisaddition.commands.CommandTreeContext;
import carpettisaddition.commands.manipulate.AbstractManipulator;
import carpettisaddition.utils.Messenger;
import carpettisaddition.utils.PositionUtils;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.level.ChunkPos;

import java.util.List;
import java.util.function.BiFunction;

import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;
import static net.minecraft.commands.arguments.DimensionTypeArgument.dimension;
import static net.minecraft.commands.arguments.DimensionTypeArgument.getDimension;

//#if MC >= 11802
//$$ import carpettisaddition.mixins.command.manipulate.chunk.ChunkMapSaveCooldownAccessor;
//$$ import carpettisaddition.utils.CommandUtils;
//$$ import carpettisaddition.utils.compat.DimensionWrapper;
//$$ import com.mojang.brigadier.context.CommandContext;
//$$ import it.unimi.dsi.fastutil.longs.Long2LongMap;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import static com.mojang.brigadier.arguments.LongArgumentType.getLong;
//$$ import static com.mojang.brigadier.arguments.LongArgumentType.longArg;
//#endif

public class ChunkManipulator extends AbstractManipulator
{
	public ChunkManipulator()
	{
		super("chunk");
	}

	// ============================== Command ==============================

	@SuppressWarnings("SameParameterValue")
	private LiteralArgumentBuilder<CommandSourceStack> makeBatchOperationNode(ThrottledOperator operator, String subcommand, int maxRangeRadius)
	{
		BiFunction<String, BiFunction<CommandSourceStack, Integer, Integer>, LiteralArgumentBuilder<CommandSourceStack>> makeRadiusNode = (name, func) ->
				literal(name).
						then(argument("radius", integer(0, maxRangeRadius)).
								executes(c -> func.apply(c.getSource(), getInteger(c, "radius")))
						);

		return literal(subcommand).
				executes(c -> {
					Messenger.tell(c.getSource(), Messenger.formatting(tr("help.danger_notice"), ChatFormatting.RED));
					Messenger.tell(c.getSource(), tr("help.root"));
					return 0;
				}).
				then(literal("current").
						executes(c -> operator.operateCurrent(c.getSource()))
				).
				then(makeRadiusNode.apply("square", operator::operateInSquare)).
				then(makeRadiusNode.apply("chebyshev", operator::operateInSquare)).
				then(makeRadiusNode.apply("circle", operator::operateInCircle)).
				then(makeRadiusNode.apply("euclidean", operator::operateInCircle)).
				then(literal("at").
						then(argument("chunkX", integer()).
								then(argument("chunkZ", integer()).
										executes(c -> operator.operateAt(
												c.getSource(), new ChunkPos(getInteger(c, "chunkX"), getInteger(c, "chunkZ"))
										))
								)
						)
				);
	}

	@Override
	public void buildSubCommand(CommandTreeContext.Node context)
	{
		ThrottledOperator eraseOperator = new ThrottledOperator(this, this::doEraseChunks);
		ThrottledOperator relightOperator = new ThrottledOperator(this, this::doRelightChunks);

		// TODO copy "relight" to "block manipulator" for block box impl
		context.node.
				then(makeBatchOperationNode(eraseOperator, "erase", 32)).
				then(makeBatchOperationNode(relightOperator, "relight", 32).
						then(literal("abort").executes(c -> relightOperator.abort(c.getSource())))
				);

		//#if MC >= 11802
		//$$ context.node.
		//$$ 		then(literal("save_cooldown").
		//$$ 				// /manipulate chunk save_cooldown -> query current chunk in current dimension
		//$$ 				executes(c -> querySaveCooldownCurrentChunk(c)).
		//$$ 				then(literal("query").
		//$$ 						// /manipulate chunk save_cooldown query -> query current chunk
		//$$ 						executes(c -> querySaveCooldownCurrentChunk(c)).
		//$$ 						then(argument("chunkX", integer()).
		//$$ 								then(argument("chunkZ", integer()).
		//$$ 										executes(c -> querySaveCooldown(
		//$$ 												c,
		//$$ 												getInteger(c, "chunkX"),
		//$$ 												getInteger(c, "chunkZ")
		//$$ 										)).
		//$$ 										then(literal("in").
		//$$ 												then(argument("dimension", dimension()).
		//$$ 														executes(c -> querySaveCooldown(
		//$$ 																c,
		//$$ 																getInteger(c, "chunkX"),
		//$$ 																getInteger(c, "chunkZ")
		//$$ 														))
		//$$ 												)
		//$$ 										)
		//$$ 								)
		//$$ 						)
		//$$ 				).
		//$$ 				then(literal("reset").
		//$$ 						// /manipulate chunk save_cooldown reset -> reset current chunk
		//$$ 						executes(c -> resetSaveCooldownCurrentChunk(c)).
		//$$ 						then(argument("chunkX", integer()).
		//$$ 								then(argument("chunkZ", integer()).
		//$$ 										executes(c -> resetSaveCooldown(
		//$$ 												c,
		//$$ 												getInteger(c, "chunkX"),
		//$$ 												getInteger(c, "chunkZ")
		//$$ 										)).
		//$$ 										then(literal("in").
		//$$ 												then(argument("dimension", dimension()).
		//$$ 														executes(c -> resetSaveCooldown(
		//$$ 																c,
		//$$ 																getInteger(c, "chunkX"),
		//$$ 																getInteger(c, "chunkZ")
		//$$ 														))
		//$$ 												)
		//$$ 										)
		//$$ 								)
		//$$ 						)
		//$$ 				).
		//$$ 				then(literal("set").
		//$$ 						then(argument("cooldownMs", longArg(0)).
		//$$ 								// /manipulate chunk save_cooldown set <ms> -> set current chunk
		//$$ 								executes(c -> setSaveCooldownCurrentChunk(
		//$$ 										c,
		//$$ 										getLong(c, "cooldownMs")
		//$$ 								)).
		//$$ 								then(argument("chunkX", integer()).
		//$$ 										then(argument("chunkZ", integer()).
		//$$ 												executes(c -> setSaveCooldown(
		//$$ 														c,
		//$$ 														getInteger(c, "chunkX"),
		//$$ 														getInteger(c, "chunkZ"),
		//$$ 														getLong(c, "cooldownMs")
		//$$ 												)).
		//$$ 												then(literal("in").
		//$$ 														then(argument("dimension", dimension()).
		//$$ 																executes(c -> setSaveCooldown(
		//$$ 																		c,
		//$$ 																		getInteger(c, "chunkX"),
		//$$ 																		getInteger(c, "chunkZ"),
		//$$ 																		getLong(c, "cooldownMs")
		//$$ 																))
		//$$ 														)
		//$$ 												)
		//$$ 										)
		//$$ 								)
		//$$ 						)
		//$$ 				)
		//$$ 		);
		//#endif
	}

	// ============================== Impl ==============================

	private AbortableOperation doEraseChunks(CommandSourceStack source, List<ChunkPos> chunkPosList, Runnable doneCallback)
	{
		ChunkEraser chunkEraser = new ChunkEraser(this.getTranslator().getDerivedTranslator("erase"), chunkPosList, source);
		chunkEraser.erase().thenRun(doneCallback);
		return chunkEraser;
	}

	private AbortableOperation doRelightChunks(CommandSourceStack source, List<ChunkPos> chunkPosList, Runnable doneCallback)
	{
		ChunkRelighter chunkRelighter = new ChunkRelighter(this.getTranslator().getDerivedTranslator("relight"), chunkPosList, source);
		chunkRelighter.relight().thenRun(doneCallback);
		return chunkRelighter;
	}

	//#if MC >= 11802
	//$$ // ============================== Save Cooldown ==============================
	//$$
	//$$ private ChunkPos getCurrentChunkPos(CommandSourceStack source)
	//$$ {
	//$$ 	return PositionUtils.flooredChunkPos(PositionUtils.flooredBlockPos(source.getPosition()));
	//$$ }
	//$$
	//$$ private ServerLevel getLevel(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException
	//$$ {
	//$$ 	return CommandUtils.<ServerLevel>getOptArg(() -> getDimension(ctx, "dimension")).orElse(ctx.getSource().getLevel());
	//$$ }
	//$$
	//$$ private Long2LongMap getChunkSaveCooldowns(ServerLevel level)
	//$$ {
	//$$ 	return ((ChunkMapSaveCooldownAccessor)level.getChunkSource().chunkMap).getChunkSaveCooldowns$TISCM();
	//$$ }
	//$$
	//$$ private long getCurrentTimeMs()
	//$$ {
	//#if MC >= 12103
	//$$ 	return net.minecraft.Util.getMillis();
	//#else
	//$$ 	return System.currentTimeMillis();
	//#endif
	//$$ }
	//$$
	//$$ private int querySaveCooldownCurrentChunk(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException
	//$$ {
	//$$ 	ChunkPos pos = getCurrentChunkPos(ctx.getSource());
	//$$ 	return querySaveCooldown(ctx, PositionUtils.chunkPosX(pos), PositionUtils.chunkPosZ(pos));
	//$$ }
	//$$
	//$$ private int querySaveCooldown(CommandContext<CommandSourceStack> ctx, int chunkX, int chunkZ) throws com.mojang.brigadier.exceptions.CommandSyntaxException
	//$$ {
	//$$ 	ServerLevel level = getLevel(ctx);
	//$$ 	Long2LongMap cooldowns = getChunkSaveCooldowns(level);
	//$$ 	long chunkKey = PositionUtils.packChunkPos(chunkX, chunkZ);
	//$$ 	long cooldownTimestamp = cooldowns.getOrDefault(chunkKey, -1L);
	//$$ 	long now = getCurrentTimeMs();
	//$$ 	CommandSourceStack source = ctx.getSource();
	//$$ 	Object dimText = Messenger.dimension(DimensionWrapper.of(level));
	//$$
	//$$ 	// no entry, or cooldown already expired
	//$$ 	if (cooldownTimestamp == -1L || now >= cooldownTimestamp)
	//$$ 	{
	//$$ 		Messenger.tell(source, tr(
	//$$ 				"save_cooldown.query.no_cooldown",
	//$$ 				chunkX, chunkZ, dimText
	//$$ 		));
	//$$ 	}
	//$$ 	else
	//$$ 	{
	//$$ 		long remainingMs = cooldownTimestamp - now;
	//$$ 		Messenger.tell(source, tr(
	//$$ 				"save_cooldown.query.has_cooldown",
	//$$ 				chunkX, chunkZ,
	//$$ 				Messenger.formatting(Messenger.s(remainingMs + "ms"), ChatFormatting.RED),
	//$$ 				dimText
	//$$ 		));
	//$$ 	}
	//$$ 	return 0;
	//$$ }
	//$$
	//$$ private int resetSaveCooldownCurrentChunk(CommandContext<CommandSourceStack> ctx) throws com.mojang.brigadier.exceptions.CommandSyntaxException
	//$$ {
	//$$ 	ChunkPos pos = getCurrentChunkPos(ctx.getSource());
	//$$ 	return resetSaveCooldown(ctx, PositionUtils.chunkPosX(pos), PositionUtils.chunkPosZ(pos));
	//$$ }
	//$$
	//$$ private int resetSaveCooldown(CommandContext<CommandSourceStack> ctx, int chunkX, int chunkZ) throws com.mojang.brigadier.exceptions.CommandSyntaxException
	//$$ {
	//$$ 	ServerLevel level = getLevel(ctx);
	//$$ 	Long2LongMap cooldowns = getChunkSaveCooldowns(level);
	//$$ 	long chunkKey = PositionUtils.packChunkPos(chunkX, chunkZ);
	//$$ 	cooldowns.remove(chunkKey);
	//$$ 	CommandSourceStack source = ctx.getSource();
	//$$ 	Object dimText = Messenger.dimension(DimensionWrapper.of(level));
	//$$
	//$$ 	Messenger.tell(source, tr(
	//$$ 			"save_cooldown.reset",
	//$$ 			chunkX, chunkZ, dimText
	//$$ 	));
	//$$ 	return 0;
	//$$ }
	//$$
	//$$ private int setSaveCooldownCurrentChunk(CommandContext<CommandSourceStack> ctx, long cooldownMs) throws com.mojang.brigadier.exceptions.CommandSyntaxException
	//$$ {
	//$$ 	ChunkPos pos = getCurrentChunkPos(ctx.getSource());
	//$$ 	return setSaveCooldown(ctx, PositionUtils.chunkPosX(pos), PositionUtils.chunkPosZ(pos), cooldownMs);
	//$$ }
	//$$
	//$$ private int setSaveCooldown(CommandContext<CommandSourceStack> ctx, int chunkX, int chunkZ, long cooldownMs) throws com.mojang.brigadier.exceptions.CommandSyntaxException
	//$$ {
	//$$ 	ServerLevel level = getLevel(ctx);
	//$$ 	Long2LongMap cooldowns = getChunkSaveCooldowns(level);
	//$$ 	long chunkKey = PositionUtils.packChunkPos(chunkX, chunkZ);
	//$$ 	long newTimestamp = getCurrentTimeMs() + cooldownMs;
	//$$ 	cooldowns.put(chunkKey, newTimestamp);
	//$$ 	CommandSourceStack source = ctx.getSource();
	//$$ 	Object dimText = Messenger.dimension(DimensionWrapper.of(level));
	//$$
	//$$ 	Messenger.tell(source, tr(
	//$$ 			"save_cooldown.set",
	//$$ 			chunkX, chunkZ, cooldownMs, dimText
	//$$ 	));
	//$$ 	return 0;
	//$$ }
	//#endif
}
