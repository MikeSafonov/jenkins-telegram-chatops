package com.github.mikesafonov.jenkins.telegram.chatops.bot.api;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

/**
 * @author Mike Safonov
 */
public abstract class JobMessageWithKeyboard extends MessageWithKeyboard {
    protected final String jobUrl;
    protected final String jobName;

    public JobMessageWithKeyboard(Long chatId, String text, String jobUrl, String jobName) {
        super(chatId, text);
        this.jobUrl = jobUrl;
        this.jobName = jobName;
        setReplyMarkup(getInlineKeyboardMarkup());
    }

    @Override
    protected InlineKeyboardMarkup getInlineKeyboardMarkup() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(List.of(getButtons()));
        return markupInline;
    }

    protected abstract List<InlineKeyboardButton> getButtons();
}
