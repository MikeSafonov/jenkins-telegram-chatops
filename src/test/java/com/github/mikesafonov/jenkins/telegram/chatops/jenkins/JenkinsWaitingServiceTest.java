package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.BuildDetailsNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.JobNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.QueueItemNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.RunJobJenkinsApiException;
import com.offbytwo.jenkins.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
public class JenkinsWaitingServiceTest {
    private String jobName = "Job Name";
    private QueueReference queueReference = new QueueReference("location");
    private JenkinsServerWrapper jenkinsServerWrapper;
    private JenkinsWaitingService jenkinsWaitingService;

    @BeforeEach
    void setUp() {
        jenkinsServerWrapper = mock(JenkinsServerWrapper.class);
        jenkinsWaitingService = new JenkinsWaitingService(jenkinsServerWrapper);
    }

    @Nested
    class WaitUntilJobInQueue {
        @Test
        void shouldThrowJobNotFoundJenkinsApiException() {

            when(jenkinsServerWrapper.getJobByName(jobName)).thenThrow(JobNotFoundJenkinsApiException.class);

            assertThrows(JobNotFoundJenkinsApiException.class,
                    () -> jenkinsWaitingService.waitUntilJobInQueue(jobName, queueReference));
        }

        @Test
        void shouldThrowQueueItemNotFoundJenkinsApiException() {
            JobWithDetails jobWithDetails = mock(JobWithDetails.class);
            when(jenkinsServerWrapper.getJobByName(jobName)).thenReturn(jobWithDetails);
            when(jenkinsServerWrapper.getQueueItem(queueReference)).thenThrow(QueueItemNotFoundJenkinsApiException.class);

            assertThrows(QueueItemNotFoundJenkinsApiException.class,
                    () -> jenkinsWaitingService.waitUntilJobInQueue(jobName, queueReference));
        }

        @Test
        void shouldThrowRunJobJenkinsApiExceptionBecauseJobInQueue() {
            JobWithDetails jobWithDetails = mock(JobWithDetails.class);
            QueueItem queueItem = mock(QueueItem.class);
            when(queueItem.isCancelled()).thenReturn(false);
            when(jobWithDetails.isInQueue()).thenReturn(true);
            when(jenkinsServerWrapper.getJobByName(jobName)).thenReturn(jobWithDetails);
            when(jenkinsServerWrapper.getQueueItem(queueReference)).thenReturn(queueItem);

            assertThrows(RunJobJenkinsApiException.class,
                    () -> jenkinsWaitingService.waitUntilJobInQueue(jobName, queueReference));
        }

        @Test
        void shouldExecuteSuccessBecauseItemCancelled() {
            JobWithDetails jobWithDetails = mock(JobWithDetails.class);
            QueueItem queueItem = mock(QueueItem.class);
            when(queueItem.isCancelled()).thenReturn(true);
            when(jobWithDetails.isInQueue()).thenReturn(false);
            when(jenkinsServerWrapper.getJobByName(jobName)).thenReturn(jobWithDetails);
            when(jenkinsServerWrapper.getQueueItem(queueReference)).thenReturn(queueItem);

            assertDoesNotThrow(() -> jenkinsWaitingService.waitUntilJobInQueue(jobName, queueReference));
        }

        @Test
        void shouldExecuteSuccess() {
            JobWithDetails jobWithDetails = mock(JobWithDetails.class);
            QueueItem queueItem = mock(QueueItem.class);
            when(queueItem.isCancelled()).thenReturn(false);
            when(jobWithDetails.isInQueue()).thenReturn(false);
            when(jenkinsServerWrapper.getJobByName(jobName)).thenReturn(jobWithDetails);
            when(jenkinsServerWrapper.getQueueItem(queueReference)).thenReturn(queueItem);

            assertDoesNotThrow(() -> jenkinsWaitingService.waitUntilJobInQueue(jobName, queueReference));
        }
    }

    @Nested
    class WaitUntilJobNotStarted {
        @Test
        void shouldThrowQueueItemNotFoundJenkinsApiException() {
            when(jenkinsServerWrapper.getQueueItem(queueReference)).thenThrow(QueueItemNotFoundJenkinsApiException.class);

            assertThrows(QueueItemNotFoundJenkinsApiException.class,
                    () -> jenkinsWaitingService.waitUntilJobNotStarted(jobName, queueReference));
        }

        @Test
        void shouldThrowRunJobJenkinsApiExceptionBecauseExecutableIsNull() {
            QueueItem queueItem = mock(QueueItem.class);
            when(queueItem.getExecutable()).thenReturn(null);
            when(jenkinsServerWrapper.getQueueItem(queueReference)).thenReturn(queueItem);

            assertThrows(RunJobJenkinsApiException.class,
                    () -> jenkinsWaitingService.waitUntilJobNotStarted(jobName, queueReference));
        }

        @Test
        void shouldExecuteSuccess() {
            QueueItem queueItem = mock(QueueItem.class);
            when(queueItem.getExecutable()).thenReturn(new Executable());
            when(jenkinsServerWrapper.getQueueItem(queueReference)).thenReturn(queueItem);

            assertDoesNotThrow(() -> jenkinsWaitingService.waitUntilJobNotStarted(jobName, queueReference));
        }
    }

    @Nested
    class WaitUntilJobIsBuilding {
        @Test
        void shouldThrowBuildDetailsNotFoundJenkinsApiException() throws IOException {
            Build build = mock(Build.class);
            when(build.details()).thenThrow(IOException.class);

            assertThrows(BuildDetailsNotFoundJenkinsApiException.class,
                    () -> jenkinsWaitingService.waitUntilJobIsBuilding(jobName, build));
        }

        @Test
        void shouldThrowRunJobJenkinsApiExceptionBecauseBuildIsBuilding() throws IOException {
            Build build = mock(Build.class);
            BuildWithDetails buildWithDetails = mock(BuildWithDetails.class);
            when(build.details()).thenReturn(buildWithDetails);
            when(buildWithDetails.isBuilding()).thenReturn(true);

            assertThrows(RunJobJenkinsApiException.class,
                    () -> jenkinsWaitingService.waitUntilJobIsBuilding(jobName, build));
        }

        @Test
        void shouldExecuteSuccess() throws IOException {
            Build build = mock(Build.class);
            BuildWithDetails buildWithDetails = mock(BuildWithDetails.class);
            when(build.details()).thenReturn(buildWithDetails);
            when(buildWithDetails.isBuilding()).thenReturn(false);

            assertDoesNotThrow(() -> jenkinsWaitingService.waitUntilJobIsBuilding(jobName, build));
        }
    }
}
