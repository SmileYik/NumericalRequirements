package org.eu.smileyik.numericalrequirements.luaeffect.task;

import org.bukkit.command.CommandSender;
import org.eu.smileyik.numericalrequirements.core.I18N;
import org.eu.smileyik.numericalrequirements.core.command.DefaultTabSuggest;
import org.eu.smileyik.numericalrequirements.core.command.TabSuggest;
import org.eu.smileyik.numericalrequirements.luaeffect.LuaEffectExtension;
import org.eu.smileyik.numericalrequirements.luaeffect.effect.LuaEffect;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ReloadByIdTask extends AbstractTask implements DefaultTabSuggest {

    public ReloadByIdTask(LuaEffectExtension extension) {
        super(
                "lua-effect-reload-by-id",
                I18N.tr("extension.lua-effect.lua-effect-reload-by-id.name"),
                I18N.tr("extension.lua-effect.lua-effect-reload-by-id.description"),
                extension
        );
    }

    @Override
    public void run(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(I18N.trp("command", "extension.lua-effect.lua-effect-reload.no-valid-args"));
            return;
        }
        LuaEffect luaEffect = extension.getLuaEffectById(args[0]);
        if (luaEffect == null) {
            sender.sendMessage(I18N.trp("command", "extension.lua-effect.error.not-found-id", args[0]));
            return;
        }
        try {
            extension.reloadScript(args[0]);
        } catch (Exception e) {
            sender.sendMessage(I18N.trp("command", "extension.lua-effect.error.failed-load-id", args[0]));
        }
    }

    @Override
    public boolean matches(String[] args, int commandIdx) {
        return args.length >= 2 && getId().equalsIgnoreCase(args[args.length - 2]);
    }

    @Override
    public List<String> suggest() {
        return extension.getLuaEffectList()
                .stream()
                .map(it -> it.getEntity().id)
                .collect(Collectors.toList());
    }
}
