package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.BuildDetailsNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.RunJobJenkinsApiException;
import com.offbytwo.jenkins.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * API for waiting on job change status using spring-retry library
 * https://github.com/spring-projects/spring-retry
 *
 * @author Mike Safonov
 */
@Service
@RequiredArgsConstructor
public class JenkinsWaitingService {
    private final JenkinsServerWrapper jenkinsServer;

    /**
     * Waits until job with name {@code jobName} in Jenkins queue.
     *
     * @param jobName  Jenkins job name
     * @param queueRef reference to item in Jenkins queue
     */
    @Retryable(value = {RunJobJenkinsApiException.class}, maxAttempts = 1000)
    public void waitUntilJobInQueue(String jobName, QueueReference queueRef) {
        JobWithDetails jobWithDetails = jenkinsServer.getJobByName(jobName);
        QueueItem item = jenkinsServer.getQueueItem(queueRef);
        if (isInQueue(item, jobWithDetails)) {
            throw new RunJobJenkinsApiException("Job " + jobName + " still in queue");
        }
    }

    /**
     * Wait until job with name {@code jobName} not started yet
     *
     * @param jobName  Jenkins job name
     * @param queueRef reference to item in Jenkins queue
     */
    @Retryable(value = {RunJobJenkinsApiException.class}, maxAttempts = 60)
    public void waitUntilJobNotStarted(String jobName, QueueReference queueRef) {
        QueueItem item = jenkinsServer.getQueueItem(queueRef);
        if (isNotExecutable(item)) {
            throw new RunJobJenkinsApiException("Job " + jobName + " still not executable");
        }
    }

    /**
     * Wait until job with name {@code jobName} building
     *
     * @param jobName Jenkins job name
     * @param build   Jenkins build
     */
    @Retryable(value = {RunJobJenkinsApiException.class}, exclude = {BuildDetailsNotFoundJenkinsApiException.class},
            maxAttempts = 200, backoff = @Backoff(value = 200L))
    public void waitUntilJobIsBuilding(String jobName, Build build) {
        try {
            BuildWithDetails details = build.details();
            if (details.isBuilding()) {
                throw new RunJobJenkinsApiException("Job " + jobName + " still building");
            }
        } catch (IOException e) {
            throw new BuildDetailsNotFoundJenkinsApiException("Unable to find details for job " + jobName +
                    " and build N " + build.getNumber(), e);
        }
    }

    /**
     * @param queueItem      item in Jenkins queue
     * @param jobWithDetails Jenkins job
     * @return job in queue and queue item not cancelled or not
     */
    static boolean isInQueue(QueueItem queueItem, JobWithDetails jobWithDetails) {
        return !queueItem.isCancelled() && jobWithDetails.isInQueue();
    }

    /**
     * @param queueItem item in Jenkins queue
     * @return queue item still not executable or not
     */
    static boolean isNotExecutable(QueueItem queueItem) {
        return queueItem.getExecutable() == null;
    }

}
