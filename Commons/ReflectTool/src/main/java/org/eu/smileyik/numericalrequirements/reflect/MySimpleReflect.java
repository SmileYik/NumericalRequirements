package org.eu.smileyik.numericalrequirements.reflect;

import org.eu.smileyik.numericalrequirements.debug.DebugLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public interface MySimpleReflect {
    static <T extends MySimpleReflect> T get(String path) throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        return get(path, false);
    }

    static <T extends MySimpleReflect> T getForce(String path) throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        return get(path, true);
    }

    static <T extends MySimpleReflect> T get(String path, boolean forceAccess) throws ClassNotFoundException, NoSuchFieldException, NoSuchMethodException {
        String fullPath = path;
        path = path.replaceAll("//.*?(?=[;}])", "").replace(" ", "");
        DebugLogger.debug("开始处理单个反射路径. path = %s, forceAccess = %s", path, forceAccess);
        path = path.replace("\\$", "+");
        String[] $s = path.split("\\\\$");
        String classAndOther;
        Class<?> targetClass = null;
        if ($s.length != 1) {
            targetClass = Class.forName($s[0].replace("+", "$"));
            for (int i = 1; i < $s.length - 1; i++) {
                targetClass = getClassInClass(targetClass, $s[i].replace("+", "$"));
            }
            classAndOther = $s[$s.length - 1];
        } else {
            classAndOther = $s[0];
        }

        if (classAndOther.contains("@")) {
            return (T) new ReflectField(fullPath, targetClass, classAndOther, forceAccess);
        } else if (classAndOther.contains("#")) {
            return (T) new ReflectMethod<>(fullPath, targetClass, classAndOther, forceAccess);
        } else {
            return (T) new ReflectConstructor(fullPath, targetClass, classAndOther, forceAccess);
        }
    }

    static ReflectClass getReflectClass(String path) throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        return new ReflectClass(getAll(path, false));
    }

    static ReflectClass getReflectClass(String path, boolean forceAccess) throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        return new ReflectClass(getAll(path, forceAccess));
    }

    static <T> List<T> getAll(String path) throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        return getAll(path, false);
    }

    static <T> List<T> getAllForce(String path) throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        return getAll(path, true);
    }

    static <T> List<T> getAll(String path, boolean forceAccess) throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException {
        path = (path + "\n").replaceAll("//.*?(?=[;}\n])", "").replace(" ", "").replace("\n", "");
        DebugLogger.debug("开始处理多个反射路径. path = %s, forceAccess = %s", path, forceAccess);
        if (!path.endsWith("}")) {
            DebugLogger.debug("该反射路径不为集合： %s", path);
            return List.of(get(path, forceAccess));
        }

        String prefix = path.substring(0, path.indexOf("{"));
        path = path.substring(path.indexOf("{") + 1, path.length() - 1);
        DebugLogger.debug("该反射路径除去前缀后的集合为： {%s}", path);

        List<String> elements = getElements(path);
        DebugLogger.debug("分析出集合元素为： %s", elements);

        List<T> list = new LinkedList<>();
        for (String s : elements) {
            if (!s.isEmpty()) {
                list.addAll(getAll(prefix + s, forceAccess));
            }
        }
        return list;
    }

    static ReflectClass readByResource(String path, boolean forceAccess, String ... placeholders) throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException, IOException {
        Map<String, String> map = new HashMap<>();
        for (int i = 1; i < placeholders.length; i += 2) {
            map.put(placeholders[i - 1], placeholders[i]);
        }
        return readByResource(path, map, forceAccess);
    }

    static ReflectClass readByResource(String path, Map<String, String> placeholders, boolean forceAccess) throws NoSuchFieldException, ClassNotFoundException, NoSuchMethodException, IOException {
        InputStream resourceAsStream = MySimpleReflect.class.getResourceAsStream(path);
        if (resourceAsStream == null) return null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceAsStream))) {
            StringBuilder sb = new StringBuilder();
            reader.lines().forEach(s -> sb.append(s).append("\n"));
            String string = sb.toString();
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                string = string.replace(entry.getKey(), entry.getValue());
            }
            DebugLogger.debug("read '%s': \n%s", path, string);
            return getReflectClass(string, forceAccess);
        }
    }

    private static List<String> getElements(String collection) {
        List<String> elements = null;
        if (collection.contains("}")) {
            int left = 0;
            int deep = 0;
            char[] charArray = collection.toCharArray();
            elements = new ArrayList<>();
            for (int i = 0; i < charArray.length; i++) {
                if (charArray[i] == '{') {
                    ++deep;
                } else if (charArray[i] == '}') {
                    --deep;
                    if (deep == 0) {
                        String element = collection.substring(left, i + 1);
                        left = i + 1;
                        elements.add(element);
                    }
                } else if (charArray[i] == ';' && deep == 0) {
                    String element = collection.substring(left, i);
                    left = i + 1;
                    elements.add(element);
                }
            }
        }

        if (elements == null) {
            elements = Arrays.asList(collection.split(";"));
        }
        return elements;
    }

    static Class<?> forName(String className) throws ClassNotFoundException {
        switch (className) {
            case "boolean": return boolean.class;
            case "byte": return byte.class;
            case "char": return char.class;
            case "short": return short.class;
            case "int": return int.class;
            case "long": return long.class;
            case "float": return float.class;
            case "double": return double.class;
            case "void": return void.class;
            case "boolean[]": return boolean[].class;
            case "byte[]": return byte[].class;
            case "char[]": return char[].class;
            case "short[]": return short[].class;
            case "int[]": return int[].class;
            case "long[]": return long[].class;
            case "float[]": return float[].class;
            case "double[]": return double[].class;
            default: return getClassByPath(className);
        }
    }

    static Class<?> getClassByPath(String path) throws ClassNotFoundException {
        if (path.startsWith("[")) {
            path += ";";
        } else if (path.endsWith("[]")) {
            path = String.format("[L%s;", path.substring(0, path.length() - 2));
        }
        path = path.replace("\\$", "+");
        String[] $s = path.split("\\\\$");
        Class<?> targetClass = null;
        if ($s.length != 1) {
            targetClass = Class.forName($s[0].strip().replace("+", "$"));
            for (int i = 1; i < $s.length; i++) {
                targetClass = getClassInClass(targetClass, $s[i].strip().replace("+", "$"));
            }
        } else {
            targetClass = Class.forName(path);
        }
        return targetClass;
    }

    static Class<?> getClassInClass(Class<?> clazz, String className) {
        for (Class<?> declaredClass : clazz.getDeclaredClasses()) {
            if (declaredClass.getName().equals(className)) {
                return declaredClass;
            }
        }
        return null;
    }

    String getName();

    String getOriginName();
}
