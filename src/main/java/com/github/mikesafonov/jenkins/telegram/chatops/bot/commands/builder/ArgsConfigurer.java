package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.builder;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.ArgsLengthCommandMatcher;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.ArgsMinLengthCommandMatcher;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.NoArgsCommandMatcher;

/**
 * @author Mike Safonov
 */
public class ArgsConfigurer {

    private CommandConfigurer commandConfigurer;

    public ArgsConfigurer(CommandConfigurer commandConfigurer) {
        this.commandConfigurer = commandConfigurer;
    }

    public CommandConfigurer noArgs() {
        commandConfigurer.addMatcher(new NoArgsCommandMatcher());
        return commandConfigurer;
    }

    public CommandConfigurer length(int value) {
        commandConfigurer.addMatcher(new ArgsLengthCommandMatcher(value));
        return commandConfigurer;
    }

    public CommandConfigurer minLength(int value) {
        commandConfigurer.addMatcher(new ArgsMinLengthCommandMatcher(value));
        return commandConfigurer;
    }
}
