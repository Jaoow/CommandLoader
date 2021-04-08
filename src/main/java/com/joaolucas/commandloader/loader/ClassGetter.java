package com.joaolucas.commandloader.loader;

import com.google.common.collect.Lists;
import com.google.common.reflect.ClassPath;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ClassGetter {

    @SuppressWarnings("UnstableApiUsage")
    public static List<Class<?>> getClassesFromPackage(JavaPlugin plugin, String packageName) {
        ClassLoader classLoader = plugin.getClass().getClassLoader();
        try {
            return ClassPath.from(classLoader).getTopLevelClasses()
                    .stream()
                    .filter(classInfo -> classInfo.getName().toLowerCase().startsWith(packageName))
                    .map(ClassPath.ClassInfo::load)
                    .collect(Collectors.toList());
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return Lists.newArrayList();
    }
}
