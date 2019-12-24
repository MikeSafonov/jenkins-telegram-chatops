package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.api.BuildableJobMessageWithKeyboard;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.api.FolderJobMessageWithKeyboard;
import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsService;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobNameBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

/**
 * @author Mike Safonov
 */
@Log4j2
@Service
public class ChatopsTelegramBot extends TelegramLongPollingBot {
    private static final String JOBS_COMMAND = "/jobs";
    private static final String RUN_COMMAND = "/run";

    private final TelegramBotProperties telegramBotProperties;
    private final JenkinsService jenkinsService;
    private final BotSecurityService botSecurityService;

    public ChatopsTelegramBot(DefaultBotOptions botOptions, TelegramBotProperties telegramBotProperties,
                              JenkinsService jenkinsService, BotSecurityService botSecurityService) {
        super(botOptions);
        this.telegramBotProperties = telegramBotProperties;
        this.jenkinsService = jenkinsService;
        this.botSecurityService = botSecurityService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (botSecurityService.isAllowed(update)) {
            if (update.getMessage() != null) {
                handleCommand(update.getMessage());
            }
            if (update.getCallbackQuery() != null) {
                handleQuery(update.getCallbackQuery());
            }
        } else {
            sendTextMessage(update.getMessage(), "Unauthorized request");
        }
    }

    private void handleCommand(Message telegramMessage) {
        String text = telegramMessage.getText();
        if (text.equals(JOBS_COMMAND)) {
            List<JenkinsJob> jobs = jenkinsService.getJobs();
            jobs.forEach(jenkinsJob -> processJob(telegramMessage.getChatId(), null, jenkinsJob));
        } else if (text.startsWith(RUN_COMMAND)) {
            String jobName = text.replace(RUN_COMMAND, "");
            if (jobName.isBlank()) {
                sendMarkdownTextMessage(telegramMessage.getChatId(), "Please pass job name!");
            } else {
                jobName = jobName.strip();
                jenkinsService.runJob(jobName).ifPresentOrElse(build -> sendMarkdownTextMessage(telegramMessage.getChatId(),
                        "Build started\n[launch on Jenkins](" + build.getUrl() + ")"),
                        () -> sendMarkdownTextMessage(telegramMessage.getChatId(), "Build failed"));
            }
        }
    }

    private void handleQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        if (data.startsWith("folder=")) {
            String folderName = data.replace("folder=", "");
            List<JenkinsJob> jobs = jenkinsService.getJobsInFolder(folderName);
            Long chatId = Long.valueOf(callbackQuery.getFrom().getId());
            sendMarkdownTextMessage(chatId, "Child jobs for *" + folderName + "* :");
            jobs.forEach(jenkinsJob -> processJob(chatId, folderName, jenkinsJob));
        } else if (data.startsWith("run=")) {
            String jobName = data.replace("run=", "");
            Long chatId = Long.valueOf(callbackQuery.getFrom().getId());
            jenkinsService.runJob(jobName).ifPresentOrElse(build -> sendMarkdownTextMessage(chatId,
                    "Build started\n[launch on Jenkins](" + build.getUrl() + ")"),
                    () -> sendMarkdownTextMessage(chatId, "Build failed"));
        }

    }

    private void processJob(Long chatId, String folderName, JenkinsJob jenkinsJob) {
        try {
            String textMessage = buildMessageForJob(jenkinsJob);
            String jobName = JobNameBuilder.from(jenkinsJob)
                    .inFolder(folderName)
                    .build();

            SendMessage message;
            if (jenkinsJob.isBuildable()) {
                message = new BuildableJobMessageWithKeyboard(chatId, textMessage,
                        jobName,
                        jenkinsJob.getOriginalJob().getUrl());
            } else {
                message = new FolderJobMessageWithKeyboard(chatId, textMessage,
                        jobName,
                        jenkinsJob.getOriginalJob().getUrl());
            }
            sendApiMethod(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String buildMessageForJob(JenkinsJob jenkinsJob) {
        boolean folder = jenkinsJob.isFolder();
        StringBuilder stringBuilder = new StringBuilder();
        if (folder) {
            stringBuilder
                    .append("\uD83D\uDDBF");
        } else {
            stringBuilder.append("⚫");
        }
        return stringBuilder
                .append(jenkinsJob.getOriginalJob().getName())
                .append("\n")
                .toString();
    }

    private void sendTextMessage(Message message, String text) {
        sendTextMessage(message.getChatId(), text);
    }

    private void sendTextMessage(Long chatId, String text) {
        try {
            sendApiMethod(new SendMessage(chatId, text));
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void sendMarkdownTextMessage(Long chatId, String text) {
        try {
            sendApiMethod(new SendMessage(chatId, text).enableMarkdown(true));
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getName();
    }

    @Override
    public String getBotToken() {
        return telegramBotProperties.getToken();
    }
}
