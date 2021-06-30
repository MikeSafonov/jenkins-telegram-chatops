package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

/**
 * @author Mike Safonov
 */
@FunctionalInterface
public interface CommandMatcher {

    boolean match(CommandContext context);
}
