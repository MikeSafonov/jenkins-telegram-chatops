package com.github.mikesafonov.jenkins.telegram.chatops.bot.api;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

/**
 * @author Mike Safonov
 */
public class FolderJobMessageWithKeyboard extends JobMessageWithKeyboard {
    public FolderJobMessageWithKeyboard(Long chatId, String text, String jobName, String jobUrl) {
        super(chatId, text, jobUrl, jobName);
    }

    @Override
    protected List<InlineKeyboardButton> getButtons() {
        return List.of(
                new InlineKeyboardButton().setText("Launch on Jenkins").setUrl(jobUrl),
                new InlineKeyboardButton().setText("See child jobs").setCallbackData("folder=" + jobName)
        );
    }
}
