package org.eu.smileyik.numericalrequirements.reflect;

public class ReflectClassPathBuilder {
    private final StringBuffer sb = new StringBuffer();
    private int deep = 0;

    public ReflectClassPathBuilder newGroup(String prefix) {
        appendRepeatChar(' ', deep * 4);
        deep += 1;
        sb.append(prefix).append("{\n");
        return this;
    }

    public ReflectClassPathBuilder endGroup() {
        deep -= 1;
        appendRepeatChar(' ', deep * 4);
        sb.append("}\n");
        return this;
    }

    public ReflectClassPathBuilder append(String s) {
        appendRepeatChar(' ', deep * 4);
        sb.append(s).append(";\n");
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
        return string;
    }

    private void appendRepeatChar(char c, int times) {
        while (times-- >= 0) {
            sb.append(c);
        }
    }
}
