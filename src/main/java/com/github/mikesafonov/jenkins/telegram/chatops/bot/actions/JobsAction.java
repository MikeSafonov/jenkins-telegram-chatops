package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserStateService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsService;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;
import java.util.function.Consumer;

import static com.github.mikesafonov.jenkins.telegram.chatops.bot.BotEmoji.*;
import static java.util.stream.Collectors.toList;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class JobsAction implements Consumer<CommandContext> {

    private final JenkinsService jenkinsService;
    private final UserStateService userStateService;

    @Override
    public void accept(CommandContext context) {
        String folderName = replaceLeadingEmoji(context.getCommandText(), FOLDER_UNICODE);
        var jobs = (folderName.equals("/jobs"))
                ? jenkinsService.getJobs()
                : jenkinsService.getJobsInFolder(folderName);


        context.getSender()
                .sendTelegramMessage(
                        SendMessage.builder()
                                .chatId(context.getChatId().toString())
                                .text("Jobs: ")
                                .replyMarkup(buildReplyKeyboard(jobs))
                                .build()
                );

        userStateService.update(context.getChatId(), UserState.WAIT_COMMAND);
    }

    private ReplyKeyboard buildReplyKeyboard(List<JenkinsJob> jobs) {
        var keyboardRows = jobs.stream()
                .map(this::jobToKeyboard)
                .collect(toList());
        return ReplyKeyboardMarkup.builder()
                .oneTimeKeyboard(false)
                .resizeKeyboard(true)
                .selective(true)
                .keyboard(keyboardRows)
                .build();
    }

    private KeyboardRow jobToKeyboard(JenkinsJob job) {
        var row = new KeyboardRow();
        var emoji = job.isFolder() ? FOLDER_UNICODE
                : RUNNABLE_UNICODE;
        row.add(emoji + " " + job.getFullName());
        return row;
    }
}
