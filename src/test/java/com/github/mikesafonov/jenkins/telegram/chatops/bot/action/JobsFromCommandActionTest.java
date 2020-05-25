package com.github.mikesafonov.jenkins.telegram.chatops.bot.action;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.MessageBuilderService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.actions.JobsFromCommandAction;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsService;
import com.github.mikesafonov.jenkins.telegram.chatops.utils.HexUtils;
import com.offbytwo.jenkins.model.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class JobsFromCommandActionTest {
    private MessageBuilderService messageBuilderService;
    private JenkinsService jenkinsService;
    private JobsFromCommandAction action;
    private String folderName = "test";
    private String folderNameHex = HexUtils.toHex(folderName);
    private Long chatId;
    private CommandContext context;
    private TelegramBotSender telegramBotSender;

    @BeforeEach
    void setUp() {
        messageBuilderService = mock(MessageBuilderService.class);
        jenkinsService = mock(JenkinsService.class);

        action = new JobsFromCommandAction(messageBuilderService, jenkinsService);

        context = mock(CommandContext.class);
        telegramBotSender = mock(TelegramBotSender.class);
        chatId = 1L;

        when(context.getCommandText()).thenReturn("/jobs_" + folderNameHex);
        when(context.getSender()).thenReturn(telegramBotSender);
        when(context.getChatId()).thenReturn(chatId);
    }

    @Test
    void shouldDecodeFolderName() {
        when(jenkinsService.getJobsInFolder(folderName)).thenReturn(emptyList());

        action.accept(context);

        verify(jenkinsService).getJobsInFolder(folderName);
    }

    @Test
    void shouldSendMessageForJobs() {
        JenkinsJob buildableJob = mock(JenkinsJob.class);
        Job originalBuildableJob = mock(Job.class);
        JenkinsJob folderJob = mock(JenkinsJob.class);
        Job originalFolderJob = mock(Job.class);
        List<JenkinsJob> jobList = List.of(buildableJob, folderJob);

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

        action.accept(context);

        String expectedMessage = "message1\n/r_746573742F6275696C6461626C654A6F62\n\nmessage2\n/j_746573742F666F6C6465724A6F62\n\n";

        verify(telegramBotSender).sendTextMessage(chatId, expectedMessage);
    }
}
