package com.joaolucas.commandloader.commands;

import com.joaolucas.commandloader.CommandLoader;
import com.joaolucas.commandloader.commands.subcommands.SubCommand;
import com.joaolucas.commandloader.loader.ClassGetter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

public class CommandManager implements CommandExecutor {

    private final Set<SubCommand> commands = new HashSet<>();
    private final JavaPlugin main;
    private final BiConsumer<CommandSender, Set<SubCommand>> defaultCommand;

    public CommandManager(CommandLoader main, String command, BiConsumer<CommandSender, Set<SubCommand>> defaultCommand) {
        this.main = main;
        main.getCommand(command).setExecutor(this);
        this.defaultCommand = defaultCommand;
    }

    public CommandManager(JavaPlugin main, String command) {
        this.main = main;
        main.getCommand(command).setExecutor(this);
        this.defaultCommand = null;
    }

    public void loadSubCommands(String commandsPackage) {
        ClassGetter.getClassesForPackage(main, commandsPackage).forEach(this::register);
    }

    private SubCommand get(String name) {
        for (SubCommand subCommand : this.commands) {
            if (subCommand.name().equalsIgnoreCase(name)) {
                return subCommand;
            }
            String[] aliases = subCommand.aliases();
            for (String alias : aliases) {
                if (name.equalsIgnoreCase(alias)) {
                    return subCommand;
                }
            }
        }
        return null;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            if(this.defaultCommand != null) this.defaultCommand.accept(sender, this.commands);
            else sender.sendMessage("§cEstá faltando arguementos! Utilize: /" + command.getName() + " <subcommand>");
            return true;
        }
        SubCommand subCommand = get(args[0]);
        if (subCommand == null) {
            sender.sendMessage("§cEsse subcommand não existe!");
            return true;
        }
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        try {
            Objects.requireNonNull(subCommand).onCommand(sender, newArgs);
        } catch (Exception e) {
            sender.sendMessage("§cOcorreu uma falha ao executar esse comando!");
        }
        return false;
    }

    private void register(Class<?> classes) {
        try {
            if (SubCommand.class.isAssignableFrom(classes) && !classes.equals(SubCommand.class)) {
                SubCommand subCommand = (SubCommand) classes.newInstance();
                this.commands.add(subCommand);
                System.out.println("[CommandLoader] O sub comando '" + subCommand.name() + "' foi registrado");
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }
}
