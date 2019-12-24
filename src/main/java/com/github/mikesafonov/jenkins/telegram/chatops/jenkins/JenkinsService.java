package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.offbytwo.jenkins.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
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
        return jenkinsServer.getJobByName(folder).map(jobWithDetails -> {
                    Map<String, Job> jobs = jenkinsServer.getJobsByFolder(folder, jobWithDetails.getUrl());
                    return mapJobs(jobs);
                }
        )
                .orElseGet(Collections::emptyList);
    }

    public Optional<BuildWithDetails> runJob(String jobName) {
        return jenkinsServer.getJobByName(jobName)
                .map(job -> buildJob(job, jobName));
    }

    private List<JenkinsJob> mapJobs(Map<String, Job> jobs) {
        return jobs.values().stream()
                .map(JenkinsJob::new)
                .collect(toList());
    }

    private BuildWithDetails buildJob(Job job, String jobName) {
        try {
            QueueReference queueReference = job.build(true);
            return triggerJobAndWaitUntilFinished(jobName, queueReference);
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * see https://github.com/jenkinsci/java-client-api/issues/440
     *
     * @param jobName
     * @param queueRef
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    private BuildWithDetails triggerJobAndWaitUntilFinished(String jobName, QueueReference queueRef)
            throws IOException, InterruptedException {
        JobWithDetails job = this.jenkinsServer.getJenkinsServer().getJob(jobName);
        QueueItem queueItem = this.jenkinsServer.getJenkinsServer().getQueueItem(queueRef);

        while (!queueItem.isCancelled() && job.isInQueue()) {
            Thread.sleep(200L);
            job = this.jenkinsServer.getJenkinsServer().getJob(jobName);
            queueItem = this.jenkinsServer.getJenkinsServer().getQueueItem(queueRef);
        }

        int runs = 1;
        while (queueItem.getExecutable() == null && runs <= 60) {
            log.debug(".getExecutable() returns null, checking again ({})", runs);
            queueItem = this.jenkinsServer.getJenkinsServer().getQueueItem(queueRef);
            Thread.sleep(1000);
            runs++;
        }

        Build build = jenkinsServer.getJenkinsServer().getBuild(queueItem);
        if (queueItem.isCancelled()) {
            return build.details();
        }

        boolean isBuilding = build.details().isBuilding();
        while (isBuilding) {
            Thread.sleep(200L);
            isBuilding = build.details().isBuilding();
        }

        return build.details();
    }
}
