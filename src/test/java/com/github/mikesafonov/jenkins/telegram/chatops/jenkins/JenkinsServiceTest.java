package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.BuildDetailsNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.RunJobJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.JobWithDetailsWithProperties;
import com.offbytwo.jenkins.model.*;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
public class JenkinsServiceTest {

    private JenkinsServerWrapper jenkinsServerWrapper;
    private JenkinsWaitingService jenkinsWaitingService;
    private JobParametersResolver jobParametersResolver;
    private JenkinsService jenkinsService;

    @BeforeEach
    void setUp() {
        jenkinsServerWrapper = mock(JenkinsServerWrapper.class);
        jenkinsWaitingService = mock(JenkinsWaitingService.class);
        jobParametersResolver = mock(JobParametersResolver.class);

        jenkinsService = new JenkinsService(jenkinsServerWrapper, jenkinsWaitingService, jobParametersResolver);
    }

    @Nested
    class GetJobs {

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
    }

    @Nested
    class GetJobsInFolder {

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
    }

    @Nested
    class GetLastBuild {

        private JobWithDetails job;
        private String name = "name";

        @BeforeEach
        void setUp() {
            job = mock(JobWithDetails.class);
            when(jenkinsServerWrapper.getJobByName(name)).thenReturn(job);
        }

        @Test
        @SneakyThrows
        void shouldReturnBuildWithDetails() {
            Build build = mock(Build.class);
            BuildWithDetails buildWithDetails = mock(BuildWithDetails.class);

            when(job.getLastBuild()).thenReturn(build);
            when(build.details()).thenReturn(buildWithDetails);

            BuildWithDetails actual = jenkinsService.getLastBuild(name);

            assertThat(actual).isEqualTo(buildWithDetails);
        }

    }


    @Nested
    class RunJob {

        @Nested
        class WhenIOException {

            @Test
            @SneakyThrows
            void shouldThrowRunJobJenkinsApiException() {
                String name = "name";
                JobWithDetailsWithProperties job = mock(JobWithDetailsWithProperties.class);

                when(jenkinsServerWrapper.getJobByNameWithProperties(name)).thenReturn(job);
                when(job.build(true)).thenThrow(IOException.class);

                assertThrows(RunJobJenkinsApiException.class, () -> jenkinsService.runJob(name, emptyMap()));
            }

            @Test
            @SneakyThrows
            void shouldThrowBuildDetailsNotFoundJenkinsApiExceptionWithParameters() {
                String name = "name";
                JobWithDetailsWithProperties job = mock(JobWithDetailsWithProperties.class);

                Map<String, String> params = Map.of("one", "two");
                when(jenkinsServerWrapper.getJobByNameWithProperties(name)).thenReturn(job);
                when(jobParametersResolver.resolve(job, emptyMap())).thenReturn(params);
                when(job.build(params, true)).thenThrow(IOException.class);

                assertThrows(RunJobJenkinsApiException.class, () -> jenkinsService.runJob(name, emptyMap()));
            }

            @Test
            @SneakyThrows
            void shouldThrowBuildDetailsNotFoundJenkinsApiException() {
                String name = "name";
                JobWithDetailsWithProperties job = mock(JobWithDetailsWithProperties.class);
                QueueReference queueReference = new QueueReference("location");
                QueueItem queueItem = mock(QueueItem.class);
                Build build = mock(Build.class);

                when(jenkinsServerWrapper.getJobByNameWithProperties(name)).thenReturn(job);
                when(job.build(true)).thenReturn(queueReference);
                when(jenkinsServerWrapper.getQueueItem(queueReference)).thenReturn(queueItem);
                when(queueItem.isCancelled()).thenReturn(true);
                when(jenkinsServerWrapper.getBuild(queueItem)).thenReturn(build);
                when(build.details()).thenThrow(IOException.class);

                assertThrows(BuildDetailsNotFoundJenkinsApiException.class, () -> jenkinsService.runJob(name, emptyMap()));
            }
        }


        @Nested
        class WhenSuccess {

            private String name;
            private JobWithDetailsWithProperties job;
            private QueueReference queueReference;
            private BuildWithDetails buildWithDetails;
            private Map<String, String> params;
            private QueueItem queueItem;
            private Build build;

            @BeforeEach
            @SneakyThrows
            void setUp() {
                name = "name";
                job = mock(JobWithDetailsWithProperties.class);
                queueReference = new QueueReference("location");
                buildWithDetails = mock(BuildWithDetails.class);
                params = Map.of("one", "two");
                queueItem = mock(QueueItem.class);
                build = mock(Build.class);

                when(jobParametersResolver.resolve(job, emptyMap())).thenReturn(params);
                when(jenkinsServerWrapper.getJobByNameWithProperties(name)).thenReturn(job);
                when(job.build(params, true)).thenReturn(queueReference);
                when(jenkinsServerWrapper.getQueueItem(queueReference)).thenReturn(queueItem);
                when(jenkinsServerWrapper.getBuild(queueItem)).thenReturn(build);
                when(build.details()).thenReturn(buildWithDetails);

            }

            @Nested
            class WhenNotCancelled {

                @BeforeEach
                void setUp() {
                    when(queueItem.isCancelled()).thenReturn(false);

                }

                @Test
                @SneakyThrows
                void shouldReturnExpectedDetails() {

                    assertEquals(buildWithDetails, jenkinsService.runJob(name, emptyMap()));
                }

                @Test
                void shouldWaitUntilJobInQueue() {
                    jenkinsService.runJob(name, emptyMap());

                    verify(jenkinsWaitingService, times(1)).waitUntilJobInQueue(name, queueReference);
                }

                @Test
                void shouldWaitUntilJobNotStarted() {
                    jenkinsService.runJob(name, emptyMap());

                    verify(jenkinsWaitingService, times(1)).waitUntilJobNotStarted(name, queueReference);
                }

                @Test
                void shouldWaitUntilJobIsBuilding() {
                    jenkinsService.runJob(name, emptyMap());
                    var continuousBuild = new ContinuousBuild(name, build);

                    verify(jenkinsWaitingService, times(1)).waitUntilJobIsBuilding(continuousBuild);
                }
            }

            @Nested
            class WhenBuildCancelled {

                @BeforeEach
                void setUp() {
                    when(queueItem.isCancelled()).thenReturn(true);
                }

                @Test
                @SneakyThrows
                void shouldReturnExpectedDetails() {

                    assertEquals(buildWithDetails, jenkinsService.runJob(name, emptyMap()));
                }
            }
        }
    }
}
