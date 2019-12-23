package com.github.mikesafonov.jenkins.telegram.chatops.bot.api;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/**
 * @author Mike Safonov
 */
public abstract class MessageWithKeyboard extends SendMessage {
    public MessageWithKeyboard(Long chatId, String text) {
        super(chatId, text);
    }

    protected abstract InlineKeyboardMarkup getInlineKeyboardMarkup();
}
