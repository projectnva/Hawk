package me.islandscout.hawk.utils;

import me.islandscout.hawk.Hawk;
import me.islandscout.hawk.utils.blocks.BlockNMS;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class AdjacentBlocks {

    //TODO: Yes, I know there's a bunch of NPE issues that need fixing here. I'll do it later.

    //loop horizontally around nearby blocks about the size of a player's collision box
    //TODO: optimize this? Replace this Sist with a Set.
    public static List<Block> getBlocksInLocation(Location loc) {
        Location check = loc.clone();
        List<Block> blocks = new ArrayList<>();
        blocks.add(ServerUtils.getBlockAsync(check.add(0, 0, 0.3)));
        blocks.add(ServerUtils.getBlockAsync(check.add(0.3, 0, 0)));
        blocks.add(ServerUtils.getBlockAsync(check.add(0, 0, -0.3)));
        blocks.add(ServerUtils.getBlockAsync(check.add(0, 0, -0.3)));
        blocks.add(ServerUtils.getBlockAsync(check.add(-0.3, 0, 0)));
        blocks.add(ServerUtils.getBlockAsync(check.add(-0.3, 0, 0)));
        blocks.add(ServerUtils.getBlockAsync(check.add(0, 0, 0.3)));
        blocks.add(ServerUtils.getBlockAsync(check.add(0, 0, 0.3)));
        Block prevBlock = null;
        for(int i = blocks.size() - 1; i >= 0; i--) {
            Block currBlock = blocks.get(i);
            if(currBlock == null || currBlock.getType() == Material.AIR || (prevBlock != null && currBlock.equals(prevBlock))) {
                blocks.remove(i);
            }
            prevBlock = currBlock;
        }
        return blocks;
    }

    public static boolean blockIsAdjacent(Location loc, Material material) {
        Location check = loc.clone();
        return ServerUtils.getBlockAsync(check.add(0, 0, 0.3)).getType() == material ||
                ServerUtils.getBlockAsync(check.add(0.3, 0, 0)).getType() == material ||
                ServerUtils.getBlockAsync(check.add(0, 0, -0.3)).getType() == material ||
                ServerUtils.getBlockAsync(check.add(0, 0, -0.3)).getType() == material ||
                ServerUtils.getBlockAsync(check.add(-0.3, 0, 0)).getType() == material ||
                ServerUtils.getBlockAsync(check.add(-0.3, 0, 0)).getType() == material ||
                ServerUtils.getBlockAsync(check.add(0, 0, 0.3)).getType() == material ||
                ServerUtils.getBlockAsync(check.add(0, 0, 0.3)).getType() == material;
    }

    public static boolean matContainsStringIsAdjacent(Location loc, String name) {
        Location check = loc.clone();
        return ServerUtils.getBlockAsync(check.add(0, 0, 0.3)).getType().name().contains(name) ||
                ServerUtils.getBlockAsync(check.add(0.3, 0, 0)).getType().name().contains(name) ||
                ServerUtils.getBlockAsync(check.add(0, 0, -0.3)).getType().name().contains(name) ||
                ServerUtils.getBlockAsync(check.add(0, 0, -0.3)).getType().name().contains(name) ||
                ServerUtils.getBlockAsync(check.add(-0.3, 0, 0)).getType().name().contains(name) ||
                ServerUtils.getBlockAsync(check.add(-0.3, 0, 0)).getType().name().contains(name) ||
                ServerUtils.getBlockAsync(check.add(0, 0, 0.3)).getType().name().contains(name) ||
                ServerUtils.getBlockAsync(check.add(0, 0, 0.3)).getType().name().contains(name);
    }

    public static boolean blockAdjacentIsSolid(Location loc) {
        Location check = loc.clone();
        return ServerUtils.getBlockAsync(check.add(0, 0, 0.3)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(0.3, 0, 0)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(0, 0, -0.3)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(0, 0, -0.3)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(-0.3, 0, 0)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(-0.3, 0, 0)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(0, 0, 0.3)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(0, 0, 0.3)).getType().isSolid();
    }

    //TODO: does not recognize standing on fences (1.7-1.8) and (other small blocks in 1.8)
    //TODO: this still needs to get optimized. Replace List with Set
    //if not sure what your velocity is, just put -1 for velocity
    //if you just want to check for location, just put -1 for velocity
    public static boolean onGroundReally(Location loc, double yVelocity) {
        if(yVelocity > 0.5625) //allows stepping up short blocks, but not full blocks
            return false;
        double depth = 0.02; //Don't set this too low. The client doesn't like to send moves unless they are significant enough.
        //If too low, this might set off fly false flags when jumping on edge of blocks.
        Location check = loc.clone();
        List<Block> blocks = new ArrayList<>();
        blocks.addAll(AdjacentBlocks.getBlocksInLocation(check.add(0, -1, 0)));
        blocks.addAll(AdjacentBlocks.getBlocksInLocation(check.add(0, 1 - depth, 0)));
        Block prevBlock = null;
        for(int i = blocks.size() - 1; i >= 0; i--) {
            Block currBlock = blocks.get(i);
            if(prevBlock != null && currBlock.equals(prevBlock)) {
                blocks.remove(i);
            }
            prevBlock = currBlock;
        }

        AABB feet = new AABB(check.add(-0.3, 0, -0.3).toVector(), check.add(0.6, depth, 0.6).toVector());
        for(Block block : blocks) {
            if(block.isLiquid() || (!block.getType().isSolid() && Hawk.getServerVersion() == 8)) //TODO: handle small blocks in 1.8!
                continue;
            if(BlockNMS.getBlockNMS(block).getCollisionBox().isColliding(feet))
                return true;
        }
        return false;
    }

    public static boolean blockNearbyIsSolid(Location loc) {
        Location check = loc.clone();
        return ServerUtils.getBlockAsync(check.add(0, 0, 1)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(1, 0, 0)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(0, 0, -1)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(0, 0, -1)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(-1, 0, 0)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(-1, 0, 0)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(0, 0, 1)).getType().isSolid() ||
                ServerUtils.getBlockAsync(check.add(0, 0, 1)).getType().isSolid();
    }
}