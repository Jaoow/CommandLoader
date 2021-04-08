package com.joaolucas.commandloader.loader;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


/**
 * An {@link Loader} loader initialization
 *
 * @author J.Lucas <xfx.joao@gmail.com>
 */
public class Loader {

    private final CommandMap commandMap;
    private final JavaPlugin mainLoader;

    /**
     * Primary constructor
     *
     * @param mainLoader | your main class
     */

    @SneakyThrows
    public Loader(JavaPlugin mainLoader) {
        this.mainLoader = mainLoader;
        final Field commandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        commandMap.setAccessible(true);
        this.commandMap = (CommandMap) commandMap.get(Bukkit.getServer());
    }


    /**
     * Load all commands or listeners
     *
     * @param type        | load type
     * @param packageName | package name to search commands or listeners
     */
    public void load(Type type, String packageName) {
        switch (type) {
            case COMMANDS:
                ClassGetter.getClassesForPackage(mainLoader, packageName).forEach(classes -> {
                    try {
                        if (Command.class.isAssignableFrom(classes)) {
                            Command cmd = (Command) classes.newInstance();
                            System.out.println("[CommandLoader] O comando '" + cmd.getName() + "' foi registrado");
                            commandMap.register(cmd.getName(), cmd);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                break;
            case LISTENER:
                ClassGetter.getClassesForPackage(mainLoader, packageName).forEach(classes -> {
                    try {
                        if (Listener.class.isAssignableFrom(classes)) {
                            Listener listener = (Listener) classes.newInstance();
                            System.out.println("[CommandLoader] O evento '" + classes.getSimpleName() + "' foi registrado");
                            Bukkit.getPluginManager().registerEvents(listener, mainLoader);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                break;
        }
    }

    public enum Type {
        LISTENER, COMMANDS;
    }


    public static class ClassGetter {

        public static ArrayList<Class<?>> getClassesForPackage(JavaPlugin plugin, String packageName) {
            ArrayList<Class<?>> classes = new ArrayList<>();
            CodeSource src = plugin.getClass().getProtectionDomain().getCodeSource();
            if (src != null) {
                URL resource = src.getLocation();
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

        private static void processJar(URL resource, String packageName, ArrayList<Class<?>> classes) {
            JarFile jarFile;
            String relPath = packageName.replace('.', '/');
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
}
