package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.api.BuildableJobMessageWithKeyboard;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.api.FolderJobMessageWithKeyboard;
import com.github.mikesafonov.jenkins.telegram.chatops.config.TelegramBotProperties;
import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsService;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;
import com.offbytwo.jenkins.model.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
public class ChatopsTelegramBotTest {
    private TelegramBotProperties telegramBotProperties;
    private JenkinsService jenkinsService;
    private BotSecurityService botSecurityService;
    private TelegramBotSender telegramBotSender;
    private JobRunQueueService jobRunQueueService;
    private MessageBuilderService messageBuilderService;
    private ChatopsTelegramBot chatopsTelegramBot;

    @BeforeEach
    void setUp() {
        DefaultBotOptions defaultBotOptions = new DefaultBotOptions();
        telegramBotProperties = mock(TelegramBotProperties.class);
        jenkinsService = mock(JenkinsService.class);
        botSecurityService = mock(BotSecurityService.class);
        telegramBotSender = mock(TelegramBotSender.class);
        jobRunQueueService = mock(JobRunQueueService.class);
        messageBuilderService = mock(MessageBuilderService.class);
        chatopsTelegramBot = new ChatopsTelegramBot(defaultBotOptions, telegramBotProperties, jenkinsService,
            botSecurityService, telegramBotSender, jobRunQueueService, messageBuilderService);
    }

    @Test
    void shouldReturnExpectedToken() {
        when(telegramBotProperties.getToken()).thenReturn("token");

        assertEquals("token", chatopsTelegramBot.getBotToken());
    }

    @Test
    void shouldReturnExpectedUsername() {
        when(telegramBotProperties.getName()).thenReturn("name");

        assertEquals("name", chatopsTelegramBot.getBotUsername());
    }

    @Test
    void shouldSendUnauthorizedFromMessage() {
        Update update = mock(Update.class);
        Message message = mock(Message.class);
        Long chatId = 1L;

        when(botSecurityService.isAllowed(update)).thenReturn(false);
        when(update.getMessage()).thenReturn(message);
        when(message.getChatId()).thenReturn(chatId);

        chatopsTelegramBot.onUpdateReceived(update);

        verify(telegramBotSender).sendUnauthorized(chatId);
    }

    @Test
    void shouldSendUnauthorizedFromCallback() {
        Update update = mock(Update.class);
        CallbackQuery query = mock(CallbackQuery.class);
        User user = mock(User.class);
        Long chatId = 1L;

        when(botSecurityService.isAllowed(update)).thenReturn(false);
        when(update.getMessage()).thenReturn(null);
        when(update.getCallbackQuery()).thenReturn(query);
        when(query.getFrom()).thenReturn(user);
        when(user.getId()).thenReturn(1);

        chatopsTelegramBot.onUpdateReceived(update);

        verify(telegramBotSender).sendUnauthorized(chatId);
    }

    @Nested
    class HandleCommand {
        @Test
        void shouldSendHelpMessage() {
            String helpMessage = "This is [jenkins-telegram-chatops](https://github.com/MikeSafonov/jenkins-telegram-chatops) version 0.0.2" +
                "\n\nSupported commands:\n" +
                "*/jobs* - listing Jenkins jobs\n" +
                "*/run* _jobName_ - running specific Jenkins job\n" +
                "*/help* - prints help message";

            Update update = mock(Update.class);
            Message message = mock(Message.class);
            Long chatId = 1L;

            when(botSecurityService.isAllowed(update)).thenReturn(true);
            when(update.getMessage()).thenReturn(message);
            when(message.getChatId()).thenReturn(chatId);
            when(message.getText()).thenReturn("/help");
            when(messageBuilderService.getHelpMessage()).thenReturn(helpMessage);

            chatopsTelegramBot.onUpdateReceived(update);

            verify(telegramBotSender).sendMarkdownTextMessage(chatId, helpMessage);
        }

        @Test
        void shouldSendHelpMessageWhenHelpCommandWithBotName() {
            String botName = "bot_test";
            String helpMessage = "This is [jenkins-telegram-chatops](https://github.com/MikeSafonov/jenkins-telegram-chatops) version 0.0.2" +
                "\n\nSupported commands:\n" +
                "*/jobs* - listing Jenkins jobs\n" +
                "*/run* _jobName_ - running specific Jenkins job\n" +
                "*/help* - prints help message";

            Update update = mock(Update.class);
            Message message = mock(Message.class);
            Long chatId = 1L;

            when(botSecurityService.isAllowed(update)).thenReturn(true);
            when(telegramBotProperties.getName()).thenReturn(botName);
            when(update.getMessage()).thenReturn(message);
            when(message.getChatId()).thenReturn(chatId);
            when(message.getText()).thenReturn("/help@" + botName);
            when(messageBuilderService.getHelpMessage()).thenReturn(helpMessage);

            chatopsTelegramBot.onUpdateReceived(update);

            verify(telegramBotSender).sendMarkdownTextMessage(chatId, helpMessage);
        }

        @Test
        void shouldSendRunWithoutNameCommand() {
            String messageText = "Please pass job name!";
            Update update = mock(Update.class);
            Message message = mock(Message.class);
            Long chatId = 1L;

            when(botSecurityService.isAllowed(update)).thenReturn(true);
            when(update.getMessage()).thenReturn(message);
            when(message.getChatId()).thenReturn(chatId);
            when(message.getText()).thenReturn("/run");

            chatopsTelegramBot.onUpdateReceived(update);

            verify(telegramBotSender).sendMarkdownTextMessage(chatId, messageText);
        }

        @Test
        void shouldSendJobRegisteredCommand() {
            Update update = mock(Update.class);
            Message message = mock(Message.class);
            Long chatId = 1L;
            String jobName = "jobName";

            when(botSecurityService.isAllowed(update)).thenReturn(true);
            when(update.getMessage()).thenReturn(message);
            when(message.getChatId()).thenReturn(chatId);
            when(message.getText()).thenReturn("/run " + jobName);

            chatopsTelegramBot.onUpdateReceived(update);

            verify(jobRunQueueService).registerJob(new JobToRun(jobName.strip(), chatId));
            verify(telegramBotSender).sendMarkdownTextMessage(chatId, "Job *" + jobName.strip() + "* registered to run");
        }

        @Test
        void shouldSendMessagesForAllJobs() throws TelegramApiException {
            Update update = mock(Update.class);
            Message message = mock(Message.class);
            Long chatId = 1L;
            JenkinsJob buildableJob = mock(JenkinsJob.class);
            Job originalBuildableJob = mock(Job.class);
            JenkinsJob folderJob = mock(JenkinsJob.class);
            Job originalFolderJob = mock(Job.class);
            List<JenkinsJob> jobList = List.of(buildableJob, folderJob);

            when(botSecurityService.isAllowed(update)).thenReturn(true);
            when(update.getMessage()).thenReturn(message);
            when(message.getChatId()).thenReturn(chatId);
            when(message.getText()).thenReturn("/jobs");
            when(jenkinsService.getJobs()).thenReturn(jobList);
            when(buildableJob.getOriginalJob()).thenReturn(originalBuildableJob);
            when(buildableJob.isBuildable()).thenReturn(true);
            when(originalBuildableJob.getFullName()).thenReturn(null);
            when(originalBuildableJob.getName()).thenReturn("buildableJob");
            when(buildableJob.getUrl()).thenReturn("buildableJobUrl");
            when(folderJob.getOriginalJob()).thenReturn(originalFolderJob);
            when(folderJob.isBuildable()).thenReturn(false);
            when(originalFolderJob.getFullName()).thenReturn(null);
            when(originalFolderJob.getName()).thenReturn("folderJob");
            when(folderJob.getUrl()).thenReturn("folderJobUrl");

            when(messageBuilderService.buildMessageForJob(buildableJob)).thenReturn("message1");
            when(messageBuilderService.buildMessageForJob(folderJob)).thenReturn("message2");

            chatopsTelegramBot.onUpdateReceived(update);

            ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
            verify(telegramBotSender, times(2)).sendMethod(argumentCaptor.capture());
            List<SendMessage> allValues = argumentCaptor.getAllValues();
            assertThat(allValues).containsOnly(
                new BuildableJobMessageWithKeyboard(chatId, "message1", "buildableJob", "buildableJobUrl"),
                new FolderJobMessageWithKeyboard(chatId, "message2", "folderJob", "folderJobUrl")
            );
        }
    }

    @Nested
    class HandleQuery {
        @Test
        void shouldReturnJobsInFolder() throws TelegramApiException {
            Update update = mock(Update.class);
            CallbackQuery query = mock(CallbackQuery.class);
            User user = mock(User.class);
            Long chatId = 1L;
            String folderName = "folderName";
            JenkinsJob buildableJob = mock(JenkinsJob.class);
            Job originalBuildableJob = mock(Job.class);
            JenkinsJob folderJob = mock(JenkinsJob.class);
            Job originalFolderJob = mock(Job.class);
            List<JenkinsJob> jobList = List.of(buildableJob, folderJob);

            when(botSecurityService.isAllowed(update)).thenReturn(true);
            when(update.getMessage()).thenReturn(null);
            when(update.getCallbackQuery()).thenReturn(query);
            when(query.getFrom()).thenReturn(user);
            when(user.getId()).thenReturn(1);
            when(query.getData()).thenReturn("folder=" + folderName);
            when(jenkinsService.getJobsInFolder(folderName)).thenReturn(jobList);
            when(buildableJob.getOriginalJob()).thenReturn(originalBuildableJob);
            when(buildableJob.isBuildable()).thenReturn(true);
            when(originalBuildableJob.getFullName()).thenReturn(null);
            when(originalBuildableJob.getName()).thenReturn("buildableJob");
            when(buildableJob.getUrl()).thenReturn("buildableJobUrl");
            when(folderJob.getOriginalJob()).thenReturn(originalFolderJob);
            when(folderJob.isBuildable()).thenReturn(false);
            when(originalFolderJob.getFullName()).thenReturn(null);
            when(originalFolderJob.getName()).thenReturn("folderJob");
            when(folderJob.getUrl()).thenReturn("folderJobUrl");

            when(messageBuilderService.buildMessageForJob(buildableJob)).thenReturn("message1");
            when(messageBuilderService.buildMessageForJob(folderJob)).thenReturn("message2");

            chatopsTelegramBot.onUpdateReceived(update);

            verify(telegramBotSender).sendMarkdownTextMessage(chatId, "Child jobs for *" + folderName + "* :");
            ArgumentCaptor<SendMessage> argumentCaptor = ArgumentCaptor.forClass(SendMessage.class);
            verify(telegramBotSender, times(2)).sendMethod(argumentCaptor.capture());
            List<SendMessage> allValues = argumentCaptor.getAllValues();
            assertThat(allValues).containsOnly(
                new BuildableJobMessageWithKeyboard(chatId, "message1", folderName + "/buildableJob", "buildableJobUrl"),
                new FolderJobMessageWithKeyboard(chatId, "message2", folderName + "/folderJob", "folderJobUrl")
            );
        }

        @Test
        void shouldRunJob() {
            Update update = mock(Update.class);
            CallbackQuery query = mock(CallbackQuery.class);
            User user = mock(User.class);
            Long chatId = 1L;
            String jobName = "jobName";

            when(botSecurityService.isAllowed(update)).thenReturn(true);
            when(update.getMessage()).thenReturn(null);
            when(update.getCallbackQuery()).thenReturn(query);
            when(query.getFrom()).thenReturn(user);
            when(user.getId()).thenReturn(1);
            when(query.getData()).thenReturn("run=" + jobName);

            chatopsTelegramBot.onUpdateReceived(update);

            verify(jobRunQueueService).registerJob(new JobToRun(jobName.strip(), chatId));
            verify(telegramBotSender).sendMarkdownTextMessage(chatId, "Job *" + jobName.strip() + "* registered to run");
        }
    }
}
