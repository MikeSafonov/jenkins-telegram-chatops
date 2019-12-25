package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.BuildDetailsNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.RunJobJenkinsApiException;
import com.offbytwo.jenkins.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
public class JenkinsServiceTest {
    private JenkinsServerWrapper jenkinsServerWrapper;
    private JenkinsWaitingService jenkinsWaitingService;
    private JenkinsService jenkinsService;

    @BeforeEach
    void setUp() {
        jenkinsServerWrapper = mock(JenkinsServerWrapper.class);
        jenkinsWaitingService = mock(JenkinsWaitingService.class);
        jenkinsService = new JenkinsService(jenkinsServerWrapper, jenkinsWaitingService);
    }

    @Test
    void shouldReturnExpectedJobs() {
        Job one = new Job("job1", "url1");
        Job two = new Job("job2", "url2");
        var mapJobs = Map.of("job1", one, "job2", two);
        when(jenkinsServerWrapper.getJobs()).thenReturn(mapJobs);

        List<JenkinsJob> jobs = jenkinsService.getJobs();
        assertEquals(2, jobs.size());
        assertEquals(one, jobs.get(0).getOriginalJob());
        assertEquals(two, jobs.get(1).getOriginalJob());
    }

    @Test
    void shouldReturnExpectedJobsInFolder() {
        String folder = "folder";
        String url = "url";
        JobWithDetails job = mock(JobWithDetails.class);
        Job one = new Job("job1", "url1");
        Job two = new Job("job2", "url2");
        var mapJobs = Map.of("job1", one, "job2", two);

        when(job.getUrl()).thenReturn(url);
        when(jenkinsServerWrapper.getJobByName(folder)).thenReturn(job);
        when(jenkinsServerWrapper.getJobsByFolder(folder, url)).thenReturn(mapJobs);

        List<JenkinsJob> jobs = jenkinsService.getJobsInFolder(folder);
        assertEquals(2, jobs.size());
        assertEquals(one, jobs.get(0).getOriginalJob());
        assertEquals(two, jobs.get(1).getOriginalJob());
    }

    @Nested
    class RunJob {
        @Test
        void shouldThrowRunJobJenkinsApiExceptionBecauseIOException() throws IOException {
            String name = "name";
            JobWithDetails job = mock(JobWithDetails.class);

            when(jenkinsServerWrapper.getJobByName(name)).thenReturn(job);
            when(job.build(true)).thenThrow(IOException.class);

            assertThrows(RunJobJenkinsApiException.class, () -> jenkinsService.runJob(name));
        }

        @Test
        void shouldThrowBuildDetailsNotFoundJenkinsApiExceptionBecauseIOException() throws IOException {
            String name = "name";
            JobWithDetails job = mock(JobWithDetails.class);
            QueueReference queueReference = new QueueReference("location");
            QueueItem queueItem = mock(QueueItem.class);
            Build build = mock(Build.class);

            when(jenkinsServerWrapper.getJobByName(name)).thenReturn(job);
            when(job.build(true)).thenReturn(queueReference);
            when(jenkinsServerWrapper.getQueueItem(queueReference)).thenReturn(queueItem);
            when(queueItem.isCancelled()).thenReturn(true);
            when(jenkinsServerWrapper.getBuild(queueItem)).thenReturn(build);
            when(build.details()).thenThrow(IOException.class);

            assertThrows(BuildDetailsNotFoundJenkinsApiException.class, () -> jenkinsService.runJob(name));
        }

        @Test
        void shouldRunSuccess() throws IOException {
            String name = "name";
            JobWithDetails job = mock(JobWithDetails.class);
            QueueReference queueReference = new QueueReference("location");
            QueueItem queueItem = mock(QueueItem.class);
            Build build = mock(Build.class);
            BuildWithDetails buildWithDetails = mock(BuildWithDetails.class);

            when(jenkinsServerWrapper.getJobByName(name)).thenReturn(job);
            when(job.build(true)).thenReturn(queueReference);
            when(jenkinsServerWrapper.getQueueItem(queueReference)).thenReturn(queueItem);
            when(queueItem.isCancelled()).thenReturn(false);
            when(jenkinsServerWrapper.getBuild(queueItem)).thenReturn(build);
            when(build.details()).thenReturn(buildWithDetails);

            assertEquals(buildWithDetails, jenkinsService.runJob(name));
            verify(jenkinsWaitingService,  times(1)).waitUntilJobInQueue(name, queueReference);
            verify(jenkinsWaitingService,  times(1)).waitUntilJobNotStarted(name, queueReference);
            verify(jenkinsWaitingService,  times(1)).waitUntilJobIsBuilding(name, build);
            assertDoesNotThrow(() -> jenkinsService.runJob(name));
        }
    }
}
