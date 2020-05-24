package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author Mike Safonov
 */
@Value
@EqualsAndHashCode
public class EqualsCommandMatcher extends BaseCommandMatcher {

    private final String value;

    @Override
    public boolean match(CommandContext context) {
        Message message = context.getUpdate().getMessage();
        if (message != null) {
            return message.getText().equals(value);
        }
        return false;
    }
}
