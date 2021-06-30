package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.function.Consumer;

import static com.github.mikesafonov.jenkins.telegram.chatops.bot.BotEmoji.RUNNABLE_UNICODE;
import static com.github.mikesafonov.jenkins.telegram.chatops.bot.BotEmoji.replaceLeadingEmoji;

/**
 * @author Mike Safonov
 */
public class RunnableAction implements Consumer<CommandContext> {

    @Override
    public void accept(CommandContext context) {
        var jobName = replaceLeadingEmoji(context.getCommandText(), RUNNABLE_UNICODE);

        context.getSender()
                .sendTelegramMessage(
                        SendMessage.builder()
                                .chatId(context.getChatId().toString())
                                .text("Please select action: ")
                                .replyMarkup(buildReplyKeyboard(jobName))
                                .build()
                );
    }

    private ReplyKeyboard buildReplyKeyboard(String jobName) {
        return InlineKeyboardMarkup.builder()
                .keyboard(List.of(
                        List.of(InlineKeyboardButton.builder()
                                .text("Run")
                                .callbackData("/r " + jobName)
                                .build()),
                        List.of(InlineKeyboardButton.builder()
                                .text("Get last build")
                                .callbackData("/l " + jobName)
                                .build())
                ))
                .build();
    }
}
