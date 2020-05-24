package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.config.BuildInfo;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JenkinsJob;
import com.offbytwo.jenkins.model.Job;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
class MessageBuilderServiceTest {
    private BuildInfo buildInfo;
    private MessageBuilderService messageBuilderService;

    @BeforeEach
    void setUp() {
        buildInfo = mock(BuildInfo.class);
        messageBuilderService = new MessageBuilderService(buildInfo);
    }

    @Nested
    class GetHelpMessage {
        @Test
        void shouldSendHelpMessage() {
            String helpMessage = "This is [jenkins-telegram-chatops](https://github.com/MikeSafonov/jenkins-telegram-chatops) version 0.0.2" +
                    "\n\nSupported commands:\n" +
                    "*/jobs* - listing Jenkins jobs\n" +
                    "*/run* _jobName_ - running specific Jenkins job\n" +
                    "*/last* _jobName_ - get last build info of specific Jenkins job\n" +
                    "*/help* - prints help message";

            when(buildInfo.getVersion()).thenReturn("0.0.2");

            assertEquals(helpMessage, messageBuilderService.getHelpMessage());
        }
    }

    @Nested
    class WhenFolder {
        @Test
        void shouldBuildMessageForFolderJob() {
            JenkinsJob jenkinsJob = mock(JenkinsJob.class);
            Job job = mock(Job.class);
            String folderName = "folder";
            String expectedMessage = "\uD83D\uDCC1folder";

            when(jenkinsJob.isFolder()).thenReturn(true);
            when(jenkinsJob.getOriginalJob()).thenReturn(job);
            when(job.getName()).thenReturn(folderName);

            assertEquals(expectedMessage, messageBuilderService.buildMessageForJob(jenkinsJob));
        }
    }

    @Nested
    class WhenJob {
        @Test
        void shouldBuildMessageForNonFolderJob() {
            JenkinsJob jenkinsJob = mock(JenkinsJob.class);
            Job job = mock(Job.class);
            String folderName = "job";
            String expectedMessage = "\uD83D\uDE80job";

            when(jenkinsJob.isFolder()).thenReturn(false);
            when(jenkinsJob.getOriginalJob()).thenReturn(job);
            when(job.getName()).thenReturn(folderName);

            assertEquals(expectedMessage, messageBuilderService.buildMessageForJob(jenkinsJob));
        }
    }
}
