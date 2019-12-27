package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.config.BuildInfo;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import com.offbytwo.jenkins.model.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
public class MessageBuilderServiceTest {
    private BuildInfo buildInfo;
    private MessageBuilderService messageBuilderService;

    @BeforeEach
    void setUp() {
        buildInfo = mock(BuildInfo.class);
        messageBuilderService = new MessageBuilderService(buildInfo);
    }

    @Test
    void shouldSendHelpMessage() {
        String helpMessage = "This is [jenkins-telegram-chatops](https://github.com/MikeSafonov/jenkins-telegram-chatops) version 0.0.2" +
                "\n\nSupported commands:\n" +
                "*/jobs* - listing Jenkins jobs\n" +
                "*/run* _jobName_ - running specific Jenkins job\n" +
                "*/help* - prints help message";

        when(buildInfo.getVersion()).thenReturn("0.0.2");

        assertEquals(helpMessage, messageBuilderService.getHelpMessage());
    }

    @Test
    void shouldBuildMessageForFolderJob() {
        JenkinsJob jenkinsJob = mock(JenkinsJob.class);
        Job job = mock(Job.class);
        String folderName = "folder";
        String expectedMessage = "\uD83D\uDDBFfolder\n";

        when(jenkinsJob.isFolder()).thenReturn(true);
        when(jenkinsJob.getOriginalJob()).thenReturn(job);
        when(job.getName()).thenReturn(folderName);

        assertEquals(expectedMessage, messageBuilderService.buildMessageForJob(jenkinsJob));
    }

    @Test
    void shouldBuildMessageForNonFolderJob() {
        JenkinsJob jenkinsJob = mock(JenkinsJob.class);
        Job job = mock(Job.class);
        String folderName = "job";
        String expectedMessage = "⚫job\n";

        when(jenkinsJob.isFolder()).thenReturn(false);
        when(jenkinsJob.getOriginalJob()).thenReturn(job);
        when(job.getName()).thenReturn(folderName);

        assertEquals(expectedMessage, messageBuilderService.buildMessageForJob(jenkinsJob));
    }
}
