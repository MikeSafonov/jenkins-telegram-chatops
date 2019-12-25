package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.api.BuildableJobMessageWithKeyboard;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.api.FolderJobMessageWithKeyboard;
import com.github.mikesafonov.jenkins.telegram.chatops.config.BuildInfo;
import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsService;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobNameBuilder;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;
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
    private static final String HELP_COMMAND = "/help";

    private final TelegramBotProperties telegramBotProperties;
    private final JenkinsService jenkinsService;
    private final BotSecurityService botSecurityService;
    private final TelegramBotSender telegramBotSender;
    private final JobRunQueueService jobRunQueueService;
    private final BuildInfo buildInfo;

    public ChatopsTelegramBot(DefaultBotOptions botOptions, TelegramBotProperties telegramBotProperties,
                              JenkinsService jenkinsService, BotSecurityService botSecurityService,
                              TelegramBotSender telegramBotSender, JobRunQueueService jobRunQueueService, BuildInfo buildInfo) {
        super(botOptions);
        this.telegramBotProperties = telegramBotProperties;
        this.jenkinsService = jenkinsService;
        this.botSecurityService = botSecurityService;
        this.telegramBotSender = telegramBotSender;
        this.jobRunQueueService = jobRunQueueService;
        this.buildInfo = buildInfo;
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
            if (update.getMessage() != null) {
                telegramBotSender.sendUnauthorized(update.getMessage().getChatId());
            } else if (update.getCallbackQuery() != null) {
                telegramBotSender.sendUnauthorized(Long.valueOf(update.getCallbackQuery().getFrom().getId()));
            } else {
                log.info("Unable to detect user from " + update);
            }
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
                telegramBotSender.sendMarkdownTextMessage(telegramMessage.getChatId(), "Please pass job name!");
            } else {
                jobRunQueueService.registerJob(new JobToRun(jobName.strip(), telegramMessage.getChatId()));
            }
        } else if (text.equals(HELP_COMMAND)) {
            telegramBotSender.sendMarkdownTextMessage(telegramMessage.getChatId(),
                    "This is [jenkins-telegram-chatops](https://github.com/MikeSafonov/jenkins-telegram-chatops) version " + buildInfo.getVersion() +
                            "\n\nSupported commands:\n" +
                            "*/jobs* - listing Jenkins jobs\n" +
                            "*/run* _jobName_ - running specific Jenkins job\n" +
                            "*/help* - prints help message");
        }
    }

    private void handleQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        if (data.startsWith("folder=")) {
            String folderName = data.replace("folder=", "");
            List<JenkinsJob> jobs = jenkinsService.getJobsInFolder(folderName);
            Long chatId = Long.valueOf(callbackQuery.getFrom().getId());
            telegramBotSender.sendMarkdownTextMessage(chatId, "Child jobs for *" + folderName + "* :");
            jobs.forEach(jenkinsJob -> processJob(chatId, folderName, jenkinsJob));
        } else if (data.startsWith("run=")) {
            String jobName = data.replace("run=", "");
            Long chatId = Long.valueOf(callbackQuery.getFrom().getId());
            jobRunQueueService.registerJob(new JobToRun(jobName, chatId));
            telegramBotSender.sendMarkdownTextMessage(chatId, "Job *" + jobName + "* registered to run");
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
            telegramBotSender.sendMethod(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
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

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getName();
    }

    @Override
    public String getBotToken() {
        return telegramBotProperties.getToken();
    }
}
