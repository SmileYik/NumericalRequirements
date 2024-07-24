package org.eu.smileyik.numericalrequirements.multiblockcraft.machine.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.eu.smileyik.numericalrequirements.multiblockcraft.MultiBlockCraftExtension;
import org.eu.smileyik.numericalrequirements.multiblockcraft.machine.Machine;

public class OpenMachineGuiEvent extends MachineEvent implements Cancellable {
    private boolean cancelled;

    private final Player player;
    private final String identifier;

    public OpenMachineGuiEvent(Machine machine, Player player, String identifier) {
        super(machine);
        this.player = player;
        this.identifier = identifier;
    }

    public OpenMachineGuiEvent(boolean isAsync, Machine machine, Player player, String identifier) {
        super(MultiBlockCraftExtension.getInstance().getPlugin().getServer().isPrimaryThread(), machine);
        this.player = player;
        this.identifier = identifier;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * 获取玩家正在打开机器库存的标识符。
     * @return
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * 获取正在打开机器库存页面的玩家。
     * @return
     */
    public Player getPlayer() {
        return player;
    }
}
