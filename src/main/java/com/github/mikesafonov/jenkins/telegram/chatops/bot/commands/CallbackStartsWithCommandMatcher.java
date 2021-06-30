package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import lombok.Value;

/**
 * @author Mike Safonov
 */
@Value
public class CallbackStartsWithCommandMatcher implements CommandMatcher {

    private final String value;

    @Override
    public boolean match(CommandContext context) {
        var callback = context.getUpdate().getCallbackQuery();
        if (callback == null) {
            return false;
        }
        var message = callback.getData();
        return message != null && message.startsWith(value);
    }
}
