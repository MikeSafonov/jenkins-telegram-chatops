package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.builder;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.Command;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.EqualsCommandMatcher;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.StartsWithCommandMatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Safonov
 */
public class CommandsBuilder {

    private List<Command> commands = new ArrayList<>();

    public CommandConfigurer command(String value) {
        return new CommandConfigurer(this, new EqualsCommandMatcher(value));
    }

    public CommandConfigurer commandStartsWith(String value) {
        return new CommandConfigurer(this, new StartsWithCommandMatcher(value));
    }

    void registerCommand(Command command) {
        commands.add(command);
    }

    public List<Command> build() {
        return commands;
    }

}
