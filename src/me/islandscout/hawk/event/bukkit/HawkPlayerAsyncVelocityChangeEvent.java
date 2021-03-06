/*
 * This file is part of Hawk Anticheat.
 * Copyright (C) 2018 Hawk Development Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.islandscout.hawk.event.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

public class HawkPlayerAsyncVelocityChangeEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private Vector velocity;
    private Player player;
    private boolean additive;

    public HawkPlayerAsyncVelocityChangeEvent(Vector velocity, Player player, boolean additive) {
        super(true);
        this.velocity = velocity;
        this.player = player;
        this.additive = additive;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Vector getVelocity() {
        return velocity;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isAdditive() {
        return additive;
    }
}
