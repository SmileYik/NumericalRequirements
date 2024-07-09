package org.eu.smileyik.numericalrequirements.test.nms;

import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.eu.smileyik.numericalrequirements.core.api.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.nms.ActionBar;
import org.eu.smileyik.numericalrequirements.test.NeedTest;

@NeedTest
public class ActionBarTest implements Listener {
    @NeedTest
    public void testActionBar() {
        NumericalRequirements.getPlugin().getServer().getPluginManager().registerEvents(
                this, NumericalRequirements.getPlugin()
        );
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.hasItem()) {
            HandlerList.unregisterAll(this);
        }
        ActionBar.send(event.getPlayer(), "&b你&1好&5啊！");
    }
}
