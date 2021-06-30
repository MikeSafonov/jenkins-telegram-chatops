package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
public class EqualsCommandMatcher implements CommandMatcher {

    private final String value;

    @Override
    public boolean match(CommandContext context) {
        var message = context.getUpdate().getMessage();
        return message != null && value.equals(message.getText());
    }
}
