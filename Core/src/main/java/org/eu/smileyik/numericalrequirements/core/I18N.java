package org.eu.smileyik.numericalrequirements.core;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.Locale;

public class I18N {
    private static I18N self;

    private final NumericalRequirements plugin;
    private final ConfigurationSection defaultLanguage;
    private final ConfigurationSection autoDetectLanguage;
    private final ConfigurationSection targetLanguage;

    public I18N(NumericalRequirements plugin, String targetLocale) {
        self = this;
        this.plugin = plugin;
        defaultLanguage = getLanguageSection("zh_CN");
        if (defaultLanguage == null) {
            logLanguageFileNotFound("zh_CN");
        }
        if ("zh_CN".equals(Locale.getDefault().toString())) {
            autoDetectLanguage = null;
        } else {
            autoDetectLanguage = getLanguageSection(Locale.getDefault().toString());
            if (autoDetectLanguage == null && Locale.getDefault() != null) {
                logLanguageFileNotFound(Locale.getDefault().toString(), defaultLanguage);
            }
        }
        if ("zh_CN".equals(targetLocale) ||
                autoDetectLanguage != null && Locale.getDefault() != null && Locale.getDefault().toString().equals(targetLocale)) {
            targetLanguage = null;
        } else {
            targetLanguage = getLanguageSection(targetLocale);
            if (targetLanguage == null && targetLocale != null) {
                logLanguageFileNotFound(targetLocale, autoDetectLanguage, defaultLanguage);
            }
        }
    }

    private void logLanguageFileNotFound(String locale, ConfigurationSection ... sections) {
        String target = null;
        for (ConfigurationSection section : sections) {
            if (section != null) {
                target = section.getString("log.warning.language-not-fount", null);
                if (target != null) {
                    break;
                }
            }
        }
        if (target == null) {
            target = "Missing language: {0}";
        }
        plugin.getLogger().warning(MessageFormat.format(target, locale));
    }

    private ConfigurationSection getLanguageSection(String locale) {
        if (locale == null) return null;

        InputStream is = I18N.class.getResourceAsStream(String.format("/languages/%s.yml", locale));
        return is == null ? null : YamlConfiguration.loadConfiguration(new InputStreamReader(is));
    }

    private static String getMessage(String key) {
        if (self == null) return ChatColor.translateAlternateColorCodes('&', key);
        String target = null;
        if (self.targetLanguage != null) {
            target = self.targetLanguage.getString(key, null);
        }
        if (target == null && self.autoDetectLanguage != null) {
            target = self.autoDetectLanguage.getString(key, null);
        }
        if (target == null && self.defaultLanguage != null) {
            target = self.defaultLanguage.getString(key, null);
        }
        if (target == null) {
            target = key;
        }
        return ChatColor.translateAlternateColorCodes('&', target);
    }

    public static String tr(String key) {
        return getMessage(key);
    }

    public static String tr(String key, Object ... objs) {
        return MessageFormat.format(getMessage(key), objs);
    }

    public static String trp(String prefix, String key, Object ... objs) {
        return String.format("%s%s", tr(String.format("prefix.%s", prefix)), tr(key, objs));
    }

    public static void info(String key, Object ... objs) {
        assert self != null;
        self.plugin.getLogger().info(tr(String.format("log.info.%s", key), objs));
        DebugLogger.debug(e -> {
            DebugLogger.debug(DebugLogger.getStackTraceElement(5), tr(String.format("log.info.%s", key), objs));
        });
    }

    public static void warning(String key, Object ... objs) {
        assert self != null;
        self.plugin.getLogger().warning(tr(String.format("log.warning.%s", key), objs));
        DebugLogger.debug(e -> {
            DebugLogger.debug(DebugLogger.getStackTraceElement(5), tr(String.format("log.warning.%s", key), objs));
        });
    }

    public static void severe(String key, Object ... objs) {
        assert self != null;
        self.plugin.getLogger().severe(tr(String.format("log.severe.%s", key), objs));
        DebugLogger.debug(e -> {
            DebugLogger.debug(DebugLogger.getStackTraceElement(5), tr(String.format("log.severe.%s", key), objs));
        });
    }

    protected static void clear() {
        self = null;
    }
}
