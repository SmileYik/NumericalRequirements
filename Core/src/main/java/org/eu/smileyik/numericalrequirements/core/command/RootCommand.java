package org.eu.smileyik.numericalrequirements.core.command;

import org.bukkit.Effect;
import org.bukkit.EntityEffect;
import org.bukkit.Particle;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.eu.smileyik.numericalrequirements.core.NumericalRequirements;
import org.eu.smileyik.numericalrequirements.core.command.annotation.Command;
import org.eu.smileyik.numericalrequirements.core.command.annotation.CommandI18N;
import org.eu.smileyik.numericalrequirements.core.extension.ExtensionService;

@CommandI18N("command")
@Command(
        value = "NumericalRequirements",
        colorCode = "color",
        aliases = {"nr", "nreq"}
)
public class RootCommand {
    @CommandI18N("command.NumericalRequirements")
    @Command(
            value = "reload",
            colorCode = "color"
    )
    public void reload(CommandSender sender, String[] strs) {
        NumericalRequirements plugin = NumericalRequirements.getInstance();

        if (sender instanceof Player) {
            Player p = (Player) sender;
            p.spawnParticle(Particle.HEART, p.getEyeLocation(), 1);
            p.playEffect(EntityEffect.HURT);
        }

        plugin.onDisable();
        plugin.onEnable();
    }
}
