package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * @author Mike Safonov
 */
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Command {

    private boolean authorized;
    private CommandMatcher matcher;
    private Consumer<CommandContext> action;

    public boolean isMatch(CommandContext commandContext) {
        return matcher.match(commandContext);
    }

}
