package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import java.util.List;

/**
 * @author Mike Safonov
 */
public abstract class BaseCommandMatcher implements CommandMatcher {

    @Override
    public CommandMatcher and(CommandMatcher matcher) {
        return new AndCommandMatcher(List.of(this, matcher));
    }

    @Override
    public CommandMatcher or(CommandMatcher matcher) {
        return new OrCommandMatcher(List.of(this, matcher));
    }
}
