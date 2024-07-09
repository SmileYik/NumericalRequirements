package org.eu.smileyik.numericalrequirements.versionscript;

import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class VersionScript {
    public static String VERSION = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];

    public static String runScriptByResource(String resource) throws IOException {
        InputStream resourceAsStream = VersionScript.class.getResourceAsStream(resource);
        if (resourceAsStream == null) {
            return null;
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resourceAsStream))) {
            String script;
            String[] versions = VERSION.split("_");
            while ((script = br.readLine()) != null) {
                String ret = runScript(script, versions);
                if (ret != null) {
                    return ret.equalsIgnoreCase("null") ? null : ret;
                }
            }
            return null;
        }
    }

    public static String runScript(String scripts) {
        return runScript(scripts.split("\n"));
    }

    public static String runScript(String[] scripts) {
        String[] versions = VERSION.split("_");
        for (String script : scripts) {
            String ret = runScript(script, versions);
            if (ret != null) {
                return ret.equalsIgnoreCase("null") ? null : ret;
            }
        }
        return null;
    }

    private static String runScript(String script, String[] versions) {
        if (script.startsWith("//")) return null;
        for (int i = 0; i < versions.length; i++) {
            script = script.replace("$" + i, versions[i]);
        }
        String[] conditions = script.replace(" ", "").split(";");
        for (int i = 0; i < conditions.length - 1; i++) {
            if (!checkCondition(conditions[i])) {
                return null;
            }
        }
        return conditions[conditions.length - 1];
    }

    private static boolean checkCondition(String condition) {
        if (condition.contains(">=")) {
            String[] parts = condition.split(">=");
            return Integer.parseInt(parts[0]) >= Integer.parseInt(parts[1]);
        } else if (condition.contains("<=")) {
            String[] parts = condition.split("<=");
            return Integer.parseInt(parts[0]) <= Integer.parseInt(parts[1]);
        } else if (condition.contains("=")) {
            String[] parts = condition.split("=");
            return parts.length == 2 && parts[0].equalsIgnoreCase(parts[1]);
        } else if (condition.contains(">")) {
            String[] parts = condition.split(">");
            return Integer.parseInt(parts[0]) > Integer.parseInt(parts[1]);
        } else if (condition.contains("<")) {
            String[] parts = condition.split("<");
            return Integer.parseInt(parts[0]) < Integer.parseInt(parts[1]);
        } else return condition.equalsIgnoreCase("true");
    }
}
