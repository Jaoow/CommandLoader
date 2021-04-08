package com.joaolucas.commandloader;

import com.joaolucas.commandloader.commands.CommandManager;
import com.joaolucas.commandloader.commands.subcommands.SubCommand;
import com.joaolucas.commandloader.loader.Loader;
import lombok.SneakyThrows;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandLoader extends JavaPlugin {


    @Override
    public void onEnable() {
        Loader loader = new Loader(this);

        // Carregar todos eventos e comandos a partir de sua package
        String basePackage = this.getClass().getPackage().getName();
        loader.load(Loader.Type.COMMANDS, basePackage + ".commands");
        loader.load(Loader.Type.LISTENER, basePackage + ".listener");

        // Criar um comando com subcomandos
        new CommandManager(this, "example", ((sender, subCommands) -> {
            sender.sendMessage("Código de exemplo " + sender.getName());

            sender.sendMessage("Lista de sub comandos: ");
            for (SubCommand command : subCommands) {
                sender.sendMessage("/example " + command.name() + " - " + command.info());
            }

        })).loadSubCommands(basePackage + ".commands.subcommands");

    }
}
