package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.builder;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Mike Safonov
 */
public class CommandConfigurer {

    private final CommandsBuilder builder;
    private final List<CommandMatcher> matchers;
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

    public CommandConfigurer state(UserState state) {
        matchers.add(new StateCommandMatcher(state));
        return this;
    }


    public CommandsBuilder and() {
        var andCommandMatcher = new AndCommandMatcher(matchers);
        var command = new Command(andCommandMatcher, commandAction);
        builder.registerCommand(command);
        return builder;
    }
}
