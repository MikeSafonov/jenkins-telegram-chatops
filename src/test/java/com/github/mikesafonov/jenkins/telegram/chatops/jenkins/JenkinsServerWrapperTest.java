package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.FolderJob;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

        assertEquals(jobWithDetails, jenkinsServerWrapper.getJobByName(jobName).get());
    }

    @Test
    void shouldReturnEmptyJobBecauseIOException() throws IOException {
        String jobName = "Job name";
        when(jenkinsServer.getJob(jobName)).thenThrow(IOException.class);

        assertTrue(jenkinsServerWrapper.getJobByName(jobName).isEmpty());
    }

}
