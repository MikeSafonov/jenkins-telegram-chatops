package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
@EqualsAndHashCode(callSuper = true)
public class StartsWithCommandMatcher extends BaseCommandMatcher {

    private final String value;

    @Override
    public boolean match(CommandContext context) {
        var message = context.getUpdate().getMessage();
        return message != null && message.getText().startsWith(value);
    }
}
