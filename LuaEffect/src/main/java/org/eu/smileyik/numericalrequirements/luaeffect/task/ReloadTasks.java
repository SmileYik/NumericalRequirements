package org.eu.smileyik.numericalrequirements.luaeffect.task;

import org.bukkit.command.CommandSender;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.luaeffect.LuaEffectExtension;

public class ReloadTasks extends AbstractTask {
    public ReloadTasks(LuaEffectExtension extension) {
        super(
                "lua-effect-reload",
                I18N.tr("extension.lua-effect.lua-effect-reload.name"),
                I18N.tr("extension.lua-effect.lua-effect-reload.description"),
                extension
        );
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        extension.unregisterScripts();
        sender.sendMessage(I18N.trp("command", "extension.lua-effect.lua-effect-reload.unregistered-scripts"));
        extension.discoverScripts();
        sender.sendMessage(I18N.trp("command", "extension.lua-effect.lua-effect-reload.reload-scripts-finished"));
    }
}
