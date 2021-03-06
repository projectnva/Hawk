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

package me.islandscout.hawk.event;

import me.islandscout.hawk.HawkPlayer;
import me.islandscout.hawk.util.AABB;
import me.islandscout.hawk.util.AdjacentBlocks;
import me.islandscout.hawk.util.ClientBlock;
import me.islandscout.hawk.util.Debug;
import me.islandscout.hawk.util.packet.WrappedPacket;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PositionEvent extends Event {

    //Remember that the client only updates position/rotation information if
    //it is significant enough. Use hasDeltaPos() hasDeltaRot() when necessary.

    //Position events will not pass through checks if the player is teleporting.

    private final boolean onGround;
    private final boolean onGroundReally;
    //TODO: Have an onGroundReally boolean for the getFrom position. Should make things faster since checks don't have to compute it all the time.
    private boolean teleported;
    private Location cancelLocation;
    private boolean updatePos;
    private boolean updateRot;
    private Vector deltaPos;

    private static final Map<UUID, Location> last = new HashMap<>();
    private static final Map<UUID, Location> current = new HashMap<>();

    public PositionEvent(Player p, Location update, boolean onGround, HawkPlayer pp, WrappedPacket packet, boolean updatePos, boolean updateRot) {
        super(p, pp, packet);
        last.put(p.getUniqueId(), current.getOrDefault(p.getUniqueId(), pp.getLocation()));
        current.put(p.getUniqueId(), update);
        deltaPos = new Vector(update.getX() - getFrom().getX(), update.getY() - getFrom().getY(), update.getZ() - getFrom().getZ());
        onGroundReally = AdjacentBlocks.onGroundReally(update, deltaPos.getY(), true);
        this.updatePos = updatePos;
        this.updateRot = updateRot;
        this.onGround = onGround;
    }

    public Player getPlayer() {
        return p;
    }

    public Location getTo() {
        //how this can possibly ever return null, idek. here's a getOrDefault for now.
        return current.getOrDefault(p.getUniqueId(), pp.getLocation());
    }

    public Location getFrom() {
        return last.getOrDefault(p.getUniqueId(), pp.getLocation());
    }

    public void setTo(Location to) {
        current.put(p.getUniqueId(), to);
    }

    public void setFrom(Location from) {
        last.put(p.getUniqueId(), from);
    }

    public boolean isOnGround() {
        return onGround;
    }

    public boolean isOnGroundReally() {
        return onGroundReally;
    }

    public ClientBlock isOnClientBlock() {
        AABB feet = new AABB(getTo().toVector().add(new Vector(-0.3, -0.02, -0.3)), getTo().toVector().add(new Vector(0.3, 0, 0.3)));
        AABB aboveFeet = feet.clone();
        aboveFeet.translate(new Vector(0, 0.020001, 0));
        AABB cube = new AABB(new Vector(0, 0, 0), new Vector(1, 1, 1));
        for(ClientBlock cBlock : pp.getClientBlocks()) {
            cube.translateTo(cBlock.getLocation().toVector());
            if(cBlock.getMaterial().isSolid() && feet.isColliding(cube) && !aboveFeet.isColliding(cube))
                return cBlock;
        }
        return null;
    }

    public boolean hasTeleported() {
        return teleported;
    }

    public void setTeleported(boolean teleported) {
        this.teleported = teleported;
    }

    public Location getCancelLocation() {
        return cancelLocation;
    }

    public boolean hasDeltaPos() {
        return getTo().getX() != getFrom().getX() || getTo().getY() != getFrom().getY() || getTo().getZ() != getFrom().getZ();
    }

    public boolean hasDeltaRot() {
        return getTo().getYaw() != getFrom().getYaw() || getTo().getPitch() != getFrom().getPitch();
    }

    //Remember: even though these methods indicate whether this move has an updated pos/rot, that
    //doesn't mean the pos/rot actually changed.
    public boolean isUpdatePos() {
        return updatePos;
    }

    public boolean isUpdateRot() {
        return updateRot;
    }

    public Vector getDeltaPos() {
        return deltaPos;
    }

    public void cancelAndSetBack(Location setback) {
        if (!isCancelled()) {
            cancelLocation = setback;
            setCancelled(true);
            pp.setTeleporting(true);
            pp.setTeleportLoc(setback);
        }
    }

    public static void discardData() {
        last.clear();
        current.clear();
    }

    public static Location getLastPosition(HawkPlayer pp) {
        return current.getOrDefault(pp.getUuid(), pp.getLocation());
    }

}
