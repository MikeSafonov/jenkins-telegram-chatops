package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.builder;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mike Safonov
 */
public class CommandsBuilder {

    private final List<Command> commands = new ArrayList<>();

    public CommandConfigurer command(String value) {
        return new CommandConfigurer(this, new EqualsCommandMatcher(value));
    }

    public CommandConfigurer callbackStartsWith(String value) {
        return new CommandConfigurer(this, new CallbackStartsWithCommandMatcher(value));
    }

    public CommandConfigurer input() {
        return new CommandConfigurer(this, new InputBaseCommandMatcher());
    }

    void registerCommand(Command command) {
        commands.add(command);
    }

    public CommandConfigurer folder() {
        return new CommandConfigurer(this, new IsFolderCommandMatcher());
    }

    public CommandConfigurer runnable() {
        return new CommandConfigurer(this, new IsRunnableCommandMatcher());
    }

    public List<Command> build() {
        return commands;
    }

}
