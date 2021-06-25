package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.builder;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.AndCommandMatcher;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.Command;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Mike Safonov
 */
public class CommandConfigurer {

    private CommandsBuilder builder;
    private List<CommandMatcher> matchers;
    private Consumer<CommandContext> commandAction;

    public CommandConfigurer(CommandsBuilder builder, CommandMatcher matcher) {
        this.builder = builder;
        this.matchers = new ArrayList<>();
        matchers.add(matcher);
    }

    public CommandConfigurer action(Consumer<CommandContext> action) {
        this.commandAction = action;
        return this;
    }

    public ArgsConfigurer args() {
        return new ArgsConfigurer(this);
    }

    public CommandsBuilder and() {
        var andCommandMatcher = new AndCommandMatcher(matchers);
        var command = new Command(andCommandMatcher, commandAction);
        builder.registerCommand(command);
        return builder;
    }

    void addMatcher(CommandMatcher matcher) {
        matchers.add(matcher);
    }
}
