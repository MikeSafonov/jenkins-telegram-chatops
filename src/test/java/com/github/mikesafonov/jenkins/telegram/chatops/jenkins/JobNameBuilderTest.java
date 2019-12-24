package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.offbytwo.jenkins.model.Job;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Mike Safonov
 */
public class JobNameBuilderTest {

    @Test
    void shouldBuildFromFullName() {
        Job job = mock(Job.class);
        String fullName = "Job Full Name";
        when(job.getFullName()).thenReturn(fullName);

        assertEquals(fullName, JobNameBuilder.from(job).build());
    }

    @Test
    void shouldBuildFromName() {
        Job job = mock(Job.class);
        String name = "Job Name";
        when(job.getFullName()).thenReturn(null);
        when(job.getName()).thenReturn(name);

        assertEquals(name, JobNameBuilder.from(job).build());
    }

    @Test
    void shouldReturnFromNameAndFolder() {
        Job job = mock(Job.class);
        String name = "Job Name";
        String folder = "Folder";
        String expectedName = folder + "/" + name;
        when(job.getFullName()).thenReturn(null);
        when(job.getName()).thenReturn(name);

        assertEquals(expectedName, JobNameBuilder.from(job).inFolder(folder).build());
    }

    @Test
    void shouldBuildFromFullNameAndFolder() {
        Job job = mock(Job.class);
        String fullName = "Job Full Name";
        String folder = "Folder";
        when(job.getFullName()).thenReturn(fullName);

        assertEquals(fullName, JobNameBuilder.from(job).inFolder(folder).build());
    }
}
