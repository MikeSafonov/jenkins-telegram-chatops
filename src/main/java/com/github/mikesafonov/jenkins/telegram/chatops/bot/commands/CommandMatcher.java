package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

/**
 * @author Mike Safonov
 */
public interface CommandMatcher {

    boolean match(CommandContext context);

    CommandMatcher and(CommandMatcher matcher);

    CommandMatcher or(CommandMatcher matcher);
}
