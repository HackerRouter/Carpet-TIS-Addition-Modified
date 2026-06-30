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

package carpettisaddition.commands.chunkticket;

//#if 12004 <= MC && MC < 12005
//$$ import carpettisaddition.CarpetTISAdditionSettings;
//$$ import carpettisaddition.commands.AbstractCommand;
//$$ import carpettisaddition.commands.CommandTreeContext;
//$$ import carpettisaddition.utils.CarpetModUtil;
//$$ import carpettisaddition.utils.Messenger;
//$$ import carpettisaddition.utils.compat.DimensionWrapper;
//$$ import com.mojang.brigadier.builder.LiteralArgumentBuilder;
//$$ import net.minecraft.commands.CommandSourceStack;
//$$ import net.minecraft.util.Mth;
//$$ import net.minecraft.server.level.ServerLevel;
//$$ import net.minecraft.server.level.TicketType;
//$$ import net.minecraft.world.level.ChunkPos;
//$$ import java.util.Comparator;
//$$ import java.util.HashSet;
//$$ import java.util.Set;
//$$ import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
//$$ import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
//$$ import static net.minecraft.commands.Commands.argument;
//$$ import static net.minecraft.commands.Commands.literal;
//#endif

//#if 12004 <= MC && MC < 12005
//$$ public class ChunkTicketCommand extends AbstractCommand
//$$ {
//$$     private static final String NAME = "chunkticket";
//$$     private static final ChunkTicketCommand INSTANCE = new ChunkTicketCommand();
//$$     private static final TicketType<ChunkPos> DEBUG_REGION_TICKET = TicketType.create("tiscm_debug_region", Comparator.comparingLong(ChunkPos::toLong));
//$$
//$$     private final Set<TicketRecord> records = new HashSet<>();
//$$
//$$     private ChunkTicketCommand()
//$$     {
//$$         super(NAME);
//$$     }
//$$
//$$     public static ChunkTicketCommand getInstance()
//$$     {
//$$         return INSTANCE;
//$$     }
//$$
//$$     @Override
//$$     public void registerCommand(CommandTreeContext.Register context)
//$$     {
//$$         LiteralArgumentBuilder<CommandSourceStack> builder = literal(NAME).
//$$                 requires(s -> CarpetModUtil.canUseCommand(s, CarpetTISAdditionSettings.commandChunkTicket)).
//$$                 then(literal("add").
//$$                         then(argument("chunkX", integer()).
//$$                                 then(argument("chunkZ", integer()).
//$$                                         then(argument("radius", integer(0, 33)).
//$$                                                 executes(c -> addTicket(c.getSource(), getInteger(c, "chunkX"), getInteger(c, "chunkZ"), getInteger(c, "radius")))
//$$                                         )
//$$                                 )
//$$                         )
//$$                 ).
//$$                 then(literal("remove").
//$$                         then(argument("chunkX", integer()).
//$$                                 then(argument("chunkZ", integer()).
//$$                                         then(argument("radius", integer(0, 33)).
//$$                                                 executes(c -> removeTicket(c.getSource(), getInteger(c, "chunkX"), getInteger(c, "chunkZ"), getInteger(c, "radius")))
//$$                                         )
//$$                                 )
//$$                         )
//$$                 ).
//$$                 then(literal("query").
//$$                         then(literal("current").executes(c -> queryCurrentChunk(c.getSource()))).
//$$                         then(argument("chunkX", integer()).
//$$                                 then(argument("chunkZ", integer()).
//$$                                         executes(c -> queryTicket(c.getSource(), getInteger(c, "chunkX"), getInteger(c, "chunkZ")))
//$$                                 )
//$$                         )
//$$                 );
//$$         context.dispatcher.register(builder);
//$$     }
//$$
//$$     private int addTicket(CommandSourceStack source, int chunkX, int chunkZ, int radius)
//$$     {
//$$         ServerLevel world = source.getLevel();
//$$         ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
//$$         world.getChunkSource().addRegionTicket(DEBUG_REGION_TICKET, chunkPos, radius, chunkPos);
//$$         this.records.add(new TicketRecord(DimensionWrapper.of(world), chunkX, chunkZ, radius));
//$$         Messenger.tell(source, Messenger.s("Added debug region ticket at chunk " + chunkX + "," + chunkZ + " radius=" + radius + " centerLevel=" + (33 - radius)));
//$$         return 1;
//$$     }
//$$
//$$     private int removeTicket(CommandSourceStack source, int chunkX, int chunkZ, int radius)
//$$     {
//$$         ServerLevel world = source.getLevel();
//$$         ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
//$$         world.getChunkSource().removeRegionTicket(DEBUG_REGION_TICKET, chunkPos, radius, chunkPos);
//$$         this.records.remove(new TicketRecord(DimensionWrapper.of(world), chunkX, chunkZ, radius));
//$$         Messenger.tell(source, Messenger.s("Removed debug region ticket at chunk " + chunkX + "," + chunkZ + " radius=" + radius));
//$$         return 1;
//$$     }
//$$
//$$     private int queryCurrentChunk(CommandSourceStack source)
//$$     {
//$$         return queryTicket(source, Mth.floor(source.getPosition().x) >> 4, Mth.floor(source.getPosition().z) >> 4);
//$$     }
//$$
//$$     private int queryTicket(CommandSourceStack source, int chunkX, int chunkZ)
//$$     {
//$$         ServerLevel world = source.getLevel();
//$$         Messenger.tell(source, Messenger.s("Chunk " + chunkX + "," + chunkZ));
//$$         Messenger.tell(source, Messenger.s(world.getChunkSource().getChunkDebugData(new ChunkPos(chunkX, chunkZ))));
//$$         this.records.stream().
//$$                 filter(record -> record.dimension.equals(DimensionWrapper.of(world))).
//$$                 filter(record -> record.contains(chunkX, chunkZ)).
//$$                 sorted().
//$$                 forEach(record -> Messenger.tell(source, Messenger.s(
//$$                         "  debug ticket center=" + record.chunkX + "," + record.chunkZ + " radius=" + record.radius + " levelHere=" + record.levelAt(chunkX, chunkZ)
//$$                 )));
//$$         return 1;
//$$     }
//$$
//$$     private static class TicketRecord implements Comparable<TicketRecord>
//$$     {
//$$         private final DimensionWrapper dimension;
//$$         private final int chunkX;
//$$         private final int chunkZ;
//$$         private final int radius;
//$$
//$$         private TicketRecord(DimensionWrapper dimension, int chunkX, int chunkZ, int radius)
//$$         {
//$$             this.dimension = dimension;
//$$             this.chunkX = chunkX;
//$$             this.chunkZ = chunkZ;
//$$             this.radius = radius;
//$$         }
//$$
//$$         private boolean contains(int cx, int cz)
//$$         {
//$$             return Math.max(Math.abs(cx - this.chunkX), Math.abs(cz - this.chunkZ)) <= this.radius;
//$$         }
//$$
//$$         private int levelAt(int cx, int cz)
//$$         {
//$$             return 33 - this.radius + Math.max(Math.abs(cx - this.chunkX), Math.abs(cz - this.chunkZ));
//$$         }
//$$
//$$         @Override
//$$         public int compareTo(TicketRecord other)
//$$         {
//$$             int result = Integer.compare(this.levelAt(this.chunkX, this.chunkZ), other.levelAt(other.chunkX, other.chunkZ));
//$$             if (result != 0) return result;
//$$             result = Integer.compare(this.chunkX, other.chunkX);
//$$             if (result != 0) return result;
//$$             result = Integer.compare(this.chunkZ, other.chunkZ);
//$$             if (result != 0) return result;
//$$             return Integer.compare(this.radius, other.radius);
//$$         }
//$$
//$$         @Override
//$$         public boolean equals(Object obj)
//$$         {
//$$             if (this == obj) return true;
//$$             if (!(obj instanceof TicketRecord)) return false;
//$$             TicketRecord other = (TicketRecord) obj;
//$$             return this.chunkX == other.chunkX && this.chunkZ == other.chunkZ && this.radius == other.radius && this.dimension.equals(other.dimension);
//$$         }
//$$
//$$         @Override
//$$         public int hashCode()
//$$         {
//$$             int result = this.dimension.hashCode();
//$$             result = 31 * result + this.chunkX;
//$$             result = 31 * result + this.chunkZ;
//$$             result = 31 * result + this.radius;
//$$             return result;
//$$         }
//$$     }
//$$ }
//#else
public class ChunkTicketCommand
{
}
//#endif
