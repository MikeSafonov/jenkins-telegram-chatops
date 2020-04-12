package com.github.mikesafonov.jenkins.telegram.chatops.bot.action;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.MessageBuilderService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.RootJobsMessageAction;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsService;
import com.offbytwo.jenkins.model.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class RootJobsMessageActionTest {
    private MessageBuilderService messageBuilderService;
    private JenkinsService jenkinsService;
    private RootJobsMessageAction action;
    private Long chatId;
    private CommandContext context;
    private TelegramBotSender telegramBotSender;

    @BeforeEach
    void setUp() {
        messageBuilderService = mock(MessageBuilderService.class);
        jenkinsService = mock(JenkinsService.class);

        action = new RootJobsMessageAction(messageBuilderService, jenkinsService);

        context = mock(CommandContext.class);
        telegramBotSender = mock(TelegramBotSender.class);
        chatId = 1L;

        when(context.getCommandText()).thenReturn("/jobs");
        when(context.getSender()).thenReturn(telegramBotSender);
        when(context.getChatId()).thenReturn(chatId);
    }

    @Test
    void shouldDecodeFolderName() {
        when(jenkinsService.getJobs()).thenReturn(emptyList());

        action.accept(context);

        verify(jenkinsService).getJobs();
    }

    @Test
    void shouldSendMessageForJobs() {
        JenkinsJob buildableJob = mock(JenkinsJob.class);
        Job originalBuildableJob = mock(Job.class);
        JenkinsJob folderJob = mock(JenkinsJob.class);
        Job originalFolderJob = mock(Job.class);
        List<JenkinsJob> jobList = List.of(buildableJob, folderJob);

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

        action.accept(context);

        String expectedMessage = "message1\n/run_6275696C6461626C654A6F62\n\nmessage2\n/jobs_666F6C6465724A6F62\n\n";

        verify(telegramBotSender).sendTextMessage(chatId, expectedMessage);
    }
}