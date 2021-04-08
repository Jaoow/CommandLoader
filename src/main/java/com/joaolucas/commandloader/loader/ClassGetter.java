package com.joaolucas.commandloader.loader;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassGetter {

    public static ArrayList<Class<?>> getClassesForPackage(JavaPlugin plugin, String packageName) {
        ArrayList<Class<?>> classes = new ArrayList<>();
        CodeSource src = plugin.getClass().getProtectionDomain().getCodeSource();
        if (src != null) {
            URL resource = src.getLocation();
            resource.getPath();
            processJar(resource, packageName, classes);
        }
        ArrayList<String> names = new ArrayList<>();
        ArrayList<Class<?>> clazz = new ArrayList<>();
        for (Class<?> classy : classes) {
            names.add(classy.getSimpleName());
            clazz.add(classy);
        }
        classes.clear();
        names.sort(String.CASE_INSENSITIVE_ORDER);
        for (String s : names) {
            for (Class<?> classy2 : clazz) {
                if (classy2.getSimpleName().equals(s)) {
                    classes.add(classy2);
                    break;
                }
            }
        }
        return classes;
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Unexpected ClassNotFoundException loading class '" + className + "'");
        }
    }

    private static void processJar(URL resource, String pkgname, ArrayList<Class<?>> classes) {
        JarFile jarFile;
        String relPath = pkgname.replace('.', '/');
        String resPath = resource.getPath().replace("%20", " ");
        String jarPath = resPath.replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        try {
            jarFile = new JarFile(jarPath);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", e);
        }
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            String className = null;
            if (entryName.endsWith(".class") && entryName.startsWith(relPath) && entryName.length() > relPath.length() + "/".length())
                className = entryName.replace('/', '.').replace('\\', '.').replace(".class", "");
            if (className != null)
                classes.add(loadClass(className));
        }
    }
}
