package me.islandscout.hawk.checks;

import me.islandscout.hawk.HawkPlayer;
import me.islandscout.hawk.events.PositionEvent;
import me.islandscout.hawk.utils.Placeholder;
import org.bukkit.Location;

import java.util.List;

public abstract class AsyncMovementCheck extends AsyncCheck<PositionEvent> {

    //BYPASS WARNING:
    //Move checks must check getTo() locations, and if they rubberband, they MUST NOT rubberband to getTo() locations.
    //Checks implementing their own rubberband locations must set them to Player#getLocation() (but if handling teleportation, use getTo()),
    //since that check may not be the last one in the list. Do not change getFrom() or getTo() locations.
    //Player#getLocation() is recommended for rubberbanding for some checks since Spigot has additional movement checks after Hawk's checks.
    //A chain is as strong as its weakest link.

    public AsyncMovementCheck(String name, boolean enabled, int cancelThreshold, int flagThreshold, double vlPassMultiplier, long flagCooldown, String flag, List<String> punishCommands) {
        super(name, enabled, cancelThreshold, flagThreshold, vlPassMultiplier, flagCooldown, flag, punishCommands);
    }

    public AsyncMovementCheck(String name, String flag) {
        super(name, true, 0, 5, 0.9,  1000, flag, null);
    }

    protected void rubberband(PositionEvent event, Location setback) {
        event.cancelAndSetBack(setback);
    }

    protected void tryRubberband(PositionEvent event, Location setback) {
        if(canCancel() && event.getHawkPlayer().getVL(this) >= cancelThreshold)
            rubberband(event, setback);
    }

    protected void punishAndTryRubberband(HawkPlayer offender, PositionEvent event, Location setback, Placeholder... placeholders) {
        super.punish(offender, placeholders);
        tryRubberband(event, setback);
    }
}
