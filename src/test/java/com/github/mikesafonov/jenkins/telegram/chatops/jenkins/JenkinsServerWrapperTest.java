package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.BuildNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.JobNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.QueueItemNotFoundJenkinsApiException;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
public class JenkinsServerWrapperTest {
    private JenkinsServer jenkinsServer;
    private JenkinsServerWrapper jenkinsServerWrapper;

    @BeforeEach
    void setUp() {
        jenkinsServer = mock(JenkinsServer.class);
        jenkinsServerWrapper = new JenkinsServerWrapper(jenkinsServer);
    }

    @Test
    void shouldReturnExpectedJobs() throws IOException {
        Map<String, Job> jobMap = Map.of("Job1", new Job());
        when(jenkinsServer.getJobs()).thenReturn(jobMap);

        assertEquals(jobMap, jenkinsServerWrapper.getJobs());
    }

    @Test
    void shouldReturnEmptyJobsBecauseIOException() throws IOException {
        when(jenkinsServer.getJobs()).thenThrow(IOException.class);

        assertTrue(jenkinsServerWrapper.getJobs().isEmpty());
    }

    @Test
    void shouldReturnExpectedJobsByFolder() throws IOException {
        Map<String, Job> jobMap = Map.of("Job1", new Job());
        String folderName = "Folder name";
        String folderUrl = "Folder url";
        FolderJob folderJob = new FolderJob(folderName, folderUrl);
        when(jenkinsServer.getJobs(folderJob)).thenReturn(jobMap);

        assertEquals(jobMap, jenkinsServerWrapper.getJobsByFolder(folderName, folderUrl));
    }

    @Test
    void shouldReturnEmptyJobsByFolderBecauseIOException() throws IOException {
        String folderName = "Folder name";
        String folderUrl = "Folder url";
        when(jenkinsServer.getJobs(any(FolderJob.class))).thenThrow(IOException.class);

        assertTrue(jenkinsServerWrapper.getJobsByFolder(folderName, folderUrl).isEmpty());
    }

    @Test
    void shouldReturnExpectedJob() throws IOException {
        JobWithDetails jobWithDetails = new JobWithDetails();
        String jobName = "Job name";
        when(jenkinsServer.getJob(jobName)).thenReturn(jobWithDetails);

        assertEquals(jobWithDetails, jenkinsServerWrapper.getJobByName(jobName));
    }

    @Test
    void shouldThrowExceptionBecauseJobNull() throws IOException {
        String jobName = "Job name";
        when(jenkinsServer.getJob(jobName)).thenReturn(null);

        assertThrows(JobNotFoundJenkinsApiException.class, () -> jenkinsServerWrapper.getJobByName(jobName));
    }

    @Test
    void shouldThrowExceptionBecauseIOException() throws IOException {
        String jobName = "Job name";
        when(jenkinsServer.getJob(jobName)).thenThrow(IOException.class);

        assertThrows(JobNotFoundJenkinsApiException.class, () -> jenkinsServerWrapper.getJobByName(jobName));
    }

    @Test
    void shouldReturnExpectedQueueItem() throws IOException {
        QueueReference queueReference = new QueueReference("");
        QueueItem queueItem = new QueueItem();
        when(jenkinsServer.getQueueItem(queueReference)).thenReturn(queueItem);

        assertEquals(queueItem, jenkinsServerWrapper.getQueueItem(queueReference));
    }
    @Test
    void shouldThrowBecauseQueueItemNull() throws IOException {
        QueueReference queueReference = new QueueReference("");
        when(jenkinsServer.getQueueItem(queueReference)).thenReturn(null);

        assertThrows(QueueItemNotFoundJenkinsApiException.class, () -> jenkinsServerWrapper.getQueueItem(queueReference));
    }


    @Test
    void shouldThrowBecauseIOException() throws IOException {
        QueueReference queueReference = new QueueReference("");
        when(jenkinsServer.getQueueItem(queueReference)).thenThrow(IOException.class);

        assertThrows(QueueItemNotFoundJenkinsApiException.class, () -> jenkinsServerWrapper.getQueueItem(queueReference));
    }

    @Test
    void shouldReturnExpectedBuild() throws IOException {
        QueueItem queueItem = new QueueItem();
        Build build = new Build();
        when(jenkinsServer.getBuild(queueItem)).thenReturn(build);

        assertEquals(build, jenkinsServerWrapper.getBuild(queueItem));
    }

    @Test
    void shouldThrowBecauseBuildNull() throws IOException {
        QueueItem queueItem = new QueueItem();
        when(jenkinsServer.getBuild(queueItem)).thenReturn(null);

        assertThrows(BuildNotFoundJenkinsApiException.class, () -> jenkinsServerWrapper.getBuild(queueItem));
    }

    @Test
    void shouldThrowBecauseBuildIOException() throws IOException {
        QueueItem queueItem = new QueueItem();
        when(jenkinsServer.getBuild(queueItem)).thenThrow(IOException.class);

        assertThrows(BuildNotFoundJenkinsApiException.class, () -> jenkinsServerWrapper.getBuild(queueItem));
    }

}
