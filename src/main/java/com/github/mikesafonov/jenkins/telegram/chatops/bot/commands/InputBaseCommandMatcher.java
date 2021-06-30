package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import lombok.EqualsAndHashCode;

/**
 * @author Mike Safonov
 */
@EqualsAndHashCode
public class InputBaseCommandMatcher implements CommandMatcher {
    @Override
    public boolean match(CommandContext context) {
        return true;
    }
}
