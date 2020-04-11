package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Mike Safonov
 */
@AllArgsConstructor
public class EqualsUpdateCheck implements UpdateCheck {
    private String value;

    @Override
    public boolean support(Update update) {
        Message message = update.getMessage();
        if (message != null) {
            return message.getText().equals(value);
        }
        return false;
    }
}
