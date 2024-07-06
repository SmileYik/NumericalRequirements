package org.eu.smileyik.numericalrequirements.reflect;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

public class ReflectClassPathBuilder {
    private final StringBuffer sb = new StringBuffer();
    private int deep = 0;
    private final int baseDeep;

    public ReflectClassPathBuilder() {
        this(0);
    }

    public ReflectClassPathBuilder(int baseDeep) {
        this.baseDeep = baseDeep;
    }

    public ReflectClassPathBuilder newGroup(String prefix) {
        appendRepeatChar();
        deep += 1;
        sb.append(prefix).append("{\n");
        return this;
    }

    public ReflectClassPathBuilder endGroup() {
        deep -= 1;
        appendRepeatChar();
        sb.append("}\n");
        return this;
    }

    public ReflectClassPathBuilder append(String s) {
        appendRepeatChar();
        sb.append(s).append(";\n");
        return this;
    }

    public ReflectClassPathBuilder rawAppend(String s) {
        sb.append(s);
        return this;
    }

    public String finish() {
        if (deep != 0) {
            throw new RuntimeException("Has group not be closed: " + deep);
        }
        String string = sb.toString();
        if (string.endsWith(";\n")) {
            string = string.substring(0, string.length() - 2);
        }
        DebugLogger.debug("build a reflect path: \n %s", string);
        return string;
    }

    private void appendRepeatChar() {
        int times = getSpaceCount();
        while (times-- > 0) {
            sb.append(' ');
        }
    }

    public int getDeep() {
        return deep + baseDeep;
    }

    public int getSpaceCount() {
        return (deep + baseDeep) << 2;
    }
}
