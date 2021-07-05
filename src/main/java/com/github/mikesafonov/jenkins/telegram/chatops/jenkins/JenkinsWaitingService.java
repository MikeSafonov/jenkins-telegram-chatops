package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotInputRequester;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.BuildDetailsNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.RunJobJenkinsApiException;
import com.offbytwo.jenkins.model.JobWithDetails;
import com.offbytwo.jenkins.model.QueueItem;
import com.offbytwo.jenkins.model.QueueReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@Log4j2
@Service
@RequiredArgsConstructor
public class JenkinsWaitingService {
    private final JenkinsServerWrapper jenkinsServer;
    private final JobInputService jobInputService;
    private final TelegramBotInputRequester inputRequester;

    /**
     * Waits until job with name {@code jobName} in Jenkins queue.
     *
     * @param jobName  Jenkins job name
     * @param queueRef reference to item in Jenkins queue
     */
    @Retryable(value = {RunJobJenkinsApiException.class},
            maxAttemptsExpression = "#{${jenkins.retry.inqueue.maxAttempts}}",
            backoff = @Backoff(delayExpression = "#{${jenkins.retry.inqueue.backoff.delay}}"))
    public void waitUntilJobInQueue(String jobName, QueueReference queueRef) {
        var jobWithDetails = jenkinsServer.getJobByName(jobName);
        var item = jenkinsServer.getQueueItem(queueRef);
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
    @Retryable(value = {RunJobJenkinsApiException.class},
            maxAttemptsExpression = "#{${jenkins.retry.notstarted.maxAttempts}}",
            backoff = @Backoff(delayExpression = "#{${jenkins.retry.notstarted.backoff.delay}}"))
    public void waitUntilJobNotStarted(String jobName, QueueReference queueRef) {
        var item = jenkinsServer.getQueueItem(queueRef);
        if (isNotExecutable(item)) {
            throw new RunJobJenkinsApiException("Job " + jobName + " still not executable");
        }
    }

    /**
     * Wait until job with name {@code jobName} building
     *
     * @param build Jenkins build
     */
    @Retryable(value = {RunJobJenkinsApiException.class}, exclude = {BuildDetailsNotFoundJenkinsApiException.class},
            maxAttemptsExpression = "#{${jenkins.retry.building.maxAttempts}}",
            backoff = @Backoff(delayExpression = "#{${jenkins.retry.building.backoff.delay}}"))
    public void waitUntilJobIsBuilding(ContinuousBuild build) {
        try {
            var details = build.details();
            if (details.isBuilding()) {
                var pendingInputs = jobInputService.getPendingInputs(build.getBuild());
                if (!pendingInputs.isEmpty()) {
                    inputRequester.request(build, pendingInputs);
                }
                throw new RunJobJenkinsApiException("Job " + build.getJobName() + " still building");
            }
        } catch (IOException e) {
            throw new BuildDetailsNotFoundJenkinsApiException("Unable to find details for job " + build.getJobName() +
                    " and build N " + build.number(), e);
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
