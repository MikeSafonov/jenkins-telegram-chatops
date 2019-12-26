package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.BuildDetailsNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.RunJobJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.JobWithDetailsWithProperties;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.ParametersDefinitionProperty;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters.BooleanParameterDefinition;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters.BooleanParameterValue;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.parameters.ParameterDefinition;
import com.offbytwo.jenkins.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<Job> originalJobs = jobs.stream().map(JenkinsJob::getOriginalJob).collect(Collectors.toList());
        assertTrue(originalJobs.contains(one));
        assertTrue(originalJobs.contains(two));
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
        List<Job> originalJobs = jobs.stream().map(JenkinsJob::getOriginalJob).collect(Collectors.toList());
        assertTrue(originalJobs.contains(one));
        assertTrue(originalJobs.contains(two));
    }

    @Nested
    class RunJob {
        @Test
        void shouldThrowRunJobJenkinsApiExceptionBecauseIOException() throws IOException {
            String name = "name";
            JobWithDetailsWithProperties job = mock(JobWithDetailsWithProperties.class);

            when(jenkinsServerWrapper.getJobByNameWithProperties(name)).thenReturn(job);
            when(job.build(true)).thenThrow(IOException.class);

            assertThrows(RunJobJenkinsApiException.class, () -> jenkinsService.runJob(name));
        }

        @Test
        void shouldThrowBuildDetailsNotFoundJenkinsApiExceptionBecauseIOException() throws IOException {
            String name = "name";
            JobWithDetailsWithProperties job = mock(JobWithDetailsWithProperties.class);
            QueueReference queueReference = new QueueReference("location");
            QueueItem queueItem = mock(QueueItem.class);
            Build build = mock(Build.class);

            when(job.getParametersDefinitionProperty()).thenReturn(Optional.empty());
            when(jenkinsServerWrapper.getJobByNameWithProperties(name)).thenReturn(job);
            when(job.build(true)).thenReturn(queueReference);
            when(jenkinsServerWrapper.getQueueItem(queueReference)).thenReturn(queueItem);
            when(queueItem.isCancelled()).thenReturn(true);
            when(jenkinsServerWrapper.getBuild(queueItem)).thenReturn(build);
            when(build.details()).thenThrow(IOException.class);

            assertThrows(BuildDetailsNotFoundJenkinsApiException.class, () -> jenkinsService.runJob(name));
        }

        @Test
        void shouldThrowRunJobJenkinsApiExceptionBecauseExistParameterWithoutDefaultValue() throws IOException {
            String name = "name";
            JobWithDetailsWithProperties job = mock(JobWithDetailsWithProperties.class);
            QueueReference queueReference = new QueueReference("location");
            QueueItem queueItem = mock(QueueItem.class);
            Build build = mock(Build.class);
            BuildWithDetails buildWithDetails = mock(BuildWithDetails.class);
            ParametersDefinitionProperty parametersDefinitionProperty = mock(ParametersDefinitionProperty.class);
            List<ParameterDefinition> parameterDefinitions = List.of(
                    new BooleanParameterDefinition()
            );

            when(job.getParametersDefinitionProperty()).thenReturn(Optional.of(parametersDefinitionProperty));
            when(parametersDefinitionProperty.getParameterDefinitions()).thenReturn(parameterDefinitions);
            when(jenkinsServerWrapper.getJobByNameWithProperties(name)).thenReturn(job);
            when(job.build(true)).thenReturn(queueReference);
            when(jenkinsServerWrapper.getQueueItem(queueReference)).thenReturn(queueItem);
            when(queueItem.isCancelled()).thenReturn(false);
            when(jenkinsServerWrapper.getBuild(queueItem)).thenReturn(build);
            when(build.details()).thenReturn(buildWithDetails);

            assertThrows(RunJobJenkinsApiException.class, () -> jenkinsService.runJob(name));
        }

        @Test
        void shouldRunSuccess() throws IOException {
            String name = "name";
            JobWithDetailsWithProperties job = mock(JobWithDetailsWithProperties.class);
            QueueReference queueReference = new QueueReference("location");
            QueueItem queueItem = mock(QueueItem.class);
            Build build = mock(Build.class);
            BuildWithDetails buildWithDetails = mock(BuildWithDetails.class);

            when(job.getParametersDefinitionProperty()).thenReturn(Optional.empty());
            when(jenkinsServerWrapper.getJobByNameWithProperties(name)).thenReturn(job);
            when(job.build(true)).thenReturn(queueReference);
            when(jenkinsServerWrapper.getQueueItem(queueReference)).thenReturn(queueItem);
            when(queueItem.isCancelled()).thenReturn(false);
            when(jenkinsServerWrapper.getBuild(queueItem)).thenReturn(build);
            when(build.details()).thenReturn(buildWithDetails);

            assertEquals(buildWithDetails, jenkinsService.runJob(name));
            verify(jenkinsWaitingService, times(1)).waitUntilJobInQueue(name, queueReference);
            verify(jenkinsWaitingService, times(1)).waitUntilJobNotStarted(name, queueReference);
            verify(jenkinsWaitingService, times(1)).waitUntilJobIsBuilding(name, build);
            assertDoesNotThrow(() -> jenkinsService.runJob(name));
        }

        @Test
        void shouldRunSuccessWithParameters() throws IOException {
            String name = "name";
            JobWithDetailsWithProperties job = mock(JobWithDetailsWithProperties.class);
            QueueReference queueReference = new QueueReference("location");
            QueueItem queueItem = mock(QueueItem.class);
            Build build = mock(Build.class);
            BuildWithDetails buildWithDetails = mock(BuildWithDetails.class);
            ParametersDefinitionProperty parametersDefinitionProperty = mock(ParametersDefinitionProperty.class);
            List<ParameterDefinition> parameterDefinitions = List.of(
                    new BooleanParameterDefinition(new BooleanParameterValue("class", "test", true))
            );
            Map<String, String> params = Map.of("test", "true");

            when(job.getParametersDefinitionProperty()).thenReturn(Optional.of(parametersDefinitionProperty));
            when(parametersDefinitionProperty.getParameterDefinitions()).thenReturn(parameterDefinitions);
            when(jenkinsServerWrapper.getJobByNameWithProperties(name)).thenReturn(job);
            when(job.build(params, true)).thenReturn(queueReference);
            when(jenkinsServerWrapper.getQueueItem(queueReference)).thenReturn(queueItem);
            when(queueItem.isCancelled()).thenReturn(false);
            when(jenkinsServerWrapper.getBuild(queueItem)).thenReturn(build);
            when(build.details()).thenReturn(buildWithDetails);

            assertEquals(buildWithDetails, jenkinsService.runJob(name));
            verify(jenkinsWaitingService, times(1)).waitUntilJobInQueue(name, queueReference);
            verify(jenkinsWaitingService, times(1)).waitUntilJobNotStarted(name, queueReference);
            verify(jenkinsWaitingService, times(1)).waitUntilJobIsBuilding(name, build);
            assertDoesNotThrow(() -> jenkinsService.runJob(name));
        }
    }
}
