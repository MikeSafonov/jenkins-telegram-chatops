package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
public class JenkinsJobTest {
    private Job originalJob;
    private JenkinsJob jenkinsJob;

    @BeforeEach
    void setUp() {
        originalJob = mock(Job.class);
        jenkinsJob = new JenkinsJob(originalJob);
    }

    @Test
    void shouldReturnIsFolderTrueBecauseFolder() {
        when(originalJob.get_class()).thenReturn(JenkinsServerWrapper.FOLDER_CLASS);

        assertTrue(jenkinsJob.isFolder());
    }

    @Test
    void shouldReturnIsFolderTrueBecauseWorkflowMultibranch() {
        when(originalJob.get_class()).thenReturn(JenkinsServerWrapper.WORKFLOW_MULTIBRANCH_PROJECT_CLASS);

        assertTrue(jenkinsJob.isFolder());
    }

    @Test
    void shouldReturnIsFolderFalse() {
        when(originalJob.get_class()).thenReturn("com.some.class.Job");

        assertFalse(jenkinsJob.isFolder());
    }

    @Test
    void shouldReturnIsBuildableTrue() throws IOException {
        JobWithDetails jobWithDetails = mock(JobWithDetails.class);
        when(originalJob.details()).thenReturn(jobWithDetails);
        when(jobWithDetails.isBuildable()).thenReturn(true);

        assertTrue(jenkinsJob.isBuildable());
    }

    @Test
    void shouldReturnIsBuildableFalse() throws IOException {
        JobWithDetails jobWithDetails = mock(JobWithDetails.class);
        when(originalJob.details()).thenReturn(jobWithDetails);
        when(jobWithDetails.isBuildable()).thenReturn(false);

        assertFalse(jenkinsJob.isBuildable());
    }

    @Test
    void shouldReturnIsBuildableFalseBecauseException() throws IOException {
        when(originalJob.details()).thenThrow(IOException.class);

        assertFalse(jenkinsJob.isBuildable());
    }
}
