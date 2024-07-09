package org.eu.smileyik.numericalrequirements.nms;

import org.bukkit.Bukkit;

public interface NMS {
    String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
    String[] VERSIONS = VERSION.split("_");
    int MIDDLE_VERSION = Integer.parseInt(VERSIONS[1]);
}
