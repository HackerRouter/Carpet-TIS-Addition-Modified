/*
 * This file is part of the Carpet TIS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
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

package carpettisaddition.helpers.carpet.tweaks.rule.tntRandomRange;

import carpet.CarpetSettings;
import carpettisaddition.utils.GameUtils;

import java.util.Random;

/**
 * A wrapped Random class for controlling the result of nextFloat() during method collectBlocksAndDamageEntities
 * With this the tntRandomRange rule in Carpet works with/without any vanilla explosion optimization mod implementation
 *
 * In vanilla it only invokes this.world.random.nextFloat() in method collectBlocksAndDamageEntities, so it's fine
 * to just override the nextFloat in this class
 *
 * If other mods uses world random inside this method, the result might not be the same with vanilla, but it's
 * already modded so whatever
 *
 * mc1.14 ~ mc1.18.2: subproject 1.15.2 (main project)        <--------
 * mc1.19+          : subproject 1.19.4
 */
public class WrappedRandom extends Random
{
	private final Random random;

	private WrappedRandom(Random random)
	{
		this.random = random;
	}

	public static WrappedRandom wrap(Random random)
	{
		return new WrappedRandom(random);
	}

	public Random unwrap()
	{
		return this.random;
	}

	@Override
	public float nextFloat()
	{
		float vanillaResult = this.random.nextFloat();  // to make sure world random gets triggered too
		return CarpetSettings.tntRandomRange >= 0 && GameUtils.isOnServerThread() ? (float)CarpetSettings.tntRandomRange : vanillaResult;
	}
}
