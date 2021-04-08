package com.joaolucas.commandloader.commands.subcommands;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {

    /*
     * /command <sub-command> args[0] args[1]
     */

    public abstract boolean onCommand(CommandSender sender, String[] args);

    public abstract String name();

    public abstract String info();

    public abstract String[] aliases();

}
