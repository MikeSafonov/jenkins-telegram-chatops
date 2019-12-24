package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.offbytwo.jenkins.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.CompositeRetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;


/**
 * @author Mike Safonov
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class JenkinsService {
    private final JenkinsServerWrapper jenkinsServer;

    public List<JenkinsJob> getJobs() {
        Map<String, Job> jobs = jenkinsServer.getJobs();
        return mapJobs(jobs);
    }

    public List<JenkinsJob> getJobsInFolder(String folder) {
        JobWithDetails job = jenkinsServer.getJobByName(folder);
        Map<String, Job> jobs = jenkinsServer.getJobsByFolder(folder, job.getUrl());
        return mapJobs(jobs);
    }

    public Optional<Build> runJob(String jobName) {
        JobWithDetails jobByName = jenkinsServer.getJobByName(jobName);
        return Optional.ofNullable(buildJob(jobByName, jobName));
    }

    private List<JenkinsJob> mapJobs(Map<String, Job> jobs) {
        return jobs.values().stream()
                .map(JenkinsJob::new)
                .collect(toList());
    }

    private Build buildJob(JobWithDetails job, String jobName) {
        try {
            QueueReference queueReference = job.build(true);
            return waitUntilJobFinished(jobName, queueReference);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private Build waitUntilJobFinished(final String jobName, final QueueReference queueRef) {

        waitUntilJobInQueue(jobName, queueRef);

        waitUntilJobNotStarted(queueRef);

        QueueItem queueItem = jenkinsServer.getQueueItem(queueRef);
        return jenkinsServer.getBuild(queueItem);
//        if (queueItem.isCancelled()) {
//            return getDetails(build);
//        }
//
//        waitUntilJobIsBuilding(build);
//        return getDetails(build);
    }

    private void waitUntilJobInQueue(String jobName, QueueReference queueRef) {
        RetryTemplate retryTemplate = new RetryTemplate();
        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        CompositeRetryPolicy compositeRetryPolicy = new CompositeRetryPolicy();
        compositeRetryPolicy.setPolicies(new RetryPolicy[]{simpleRetryPolicy});
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        retryTemplate.setBackOffPolicy(new FixedBackOffPolicy());
        retryTemplate.execute(context -> {
            JobWithDetails jobWithDetails = jenkinsServer.getJobByName(jobName);
            QueueItem item = jenkinsServer.getQueueItem(queueRef);
            if (isInQueue(jobWithDetails, item)) {
                throw new RuntimeException();
            }
            return null;
        });
    }

    private void waitUntilJobNotStarted(QueueReference queueRef) {
        int maxAttempts = 60;
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(maxAttempts));
        retryTemplate.setBackOffPolicy(new FixedBackOffPolicy());
        retryTemplate.execute(context -> {
            QueueItem item = jenkinsServer.getQueueItem(queueRef);
            if (isNotExecuted(item)) {
                throw new RuntimeException();
            }
            return null;
        });
    }

    private void waitUntilJobIsBuilding(Build build) {
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(new SimpleRetryPolicy(200));
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(200);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        retryTemplate.execute(context -> {
            try {
                BuildWithDetails details = build.details();
                if (details.isBuilding()) {
                    throw new RuntimeException();
                }
                return details;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static BuildWithDetails getDetails(Build build) {
        try {
            return build.details();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean isInQueue(JobWithDetails jobWithDetails, QueueItem queueItem) {
        return !queueItem.isCancelled() && jobWithDetails.isInQueue();
    }

    static boolean isNotExecuted(QueueItem queueItem) {
        return queueItem.getExecutable() == null;
    }
}
