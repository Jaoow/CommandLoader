package com.joaolucas.commandloader.loader;

import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;


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
                ClassGetter.getClassesFromPackage(mainLoader, packageName).forEach(classes -> {
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
                ClassGetter.getClassesFromPackage(mainLoader, packageName).forEach(classes -> {
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
}
