package me.islandscout.hawk.modules;

import me.islandscout.hawk.Hawk;
import me.islandscout.hawk.HawkPlayer;
import me.islandscout.hawk.utils.ServerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Scheduler {

    private final Hawk hawk;

    public Scheduler(Hawk hawk) {
        this.hawk = hawk;
    }

    public void startSchedulers() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(hawk, new Runnable() {
            @Override
            public void run() {
                hawk.getTextLogger().updateFile();
                hawk.getSql().postBuffer();
            }
        }, 0L, 20L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(hawk, new Runnable() {
            @Override
            public void run() {
                for(Player p : Bukkit.getOnlinePlayers()) {
                    HawkPlayer pp = hawk.getHawkPlayer(p);
                    int newPing = ServerUtils.getPing(p);
                    pp.setPingJitter((short)Math.abs(newPing - pp.getPing()));
                    pp.setPing(ServerUtils.getPing(p));
                }
            }
        }, 0L, 40L);
    }
}