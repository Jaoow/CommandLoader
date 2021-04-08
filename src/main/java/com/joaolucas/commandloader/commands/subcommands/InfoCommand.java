package com.joaolucas.commandloader.commands.subcommands;

import org.bukkit.command.CommandSender;

public class InfoCommand extends SubCommand {

    @Override
    public boolean onCommand(CommandSender sender, String[] args) {
        sender.sendMessage("Está funcionando");
        return true;
    }

    @Override
    public String name() {
        return "info";
    }

    @Override
    public String info() {
        return "Informação";
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }
}