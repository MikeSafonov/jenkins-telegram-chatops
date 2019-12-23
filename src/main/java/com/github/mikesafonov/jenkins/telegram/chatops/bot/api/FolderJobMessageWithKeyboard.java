package com.github.mikesafonov.jenkins.telegram.chatops.bot.api;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

/**
 * @author Mike Safonov
 */
public class FolderJobMessageWithKeyboard extends MessageWithKeyboard {
    private final String jobUrl;
    private final String jobName;

    public FolderJobMessageWithKeyboard(Long chatId, String text, String jobName, String jobUrl) {
        super(chatId, text);
        this.jobUrl = jobUrl;
        this.jobName = jobName;
        setReplyMarkup(getInlineKeyboardMarkup());
    }

    @Override
    protected InlineKeyboardMarkup getInlineKeyboardMarkup() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> buttons = List.of(
                new InlineKeyboardButton().setText("Launch on Jenkins").setUrl(jobUrl),
                new InlineKeyboardButton().setText("See child jobs").setCallbackData("folder=" + jobName)
        );

        markupInline.setKeyboard(List.of(buttons));
        return markupInline;
    }
}
