package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.BuildDetailsNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.RunJobJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.JobWithDetailsWithProperties;
import com.offbytwo.jenkins.model.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.http.client.HttpResponseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;


/**
 * @author Mike Safonov
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class JenkinsService {

    private final JenkinsServerWrapper jenkinsServer;
    private final JenkinsWaitingService jenkinsWaitingService;
    private final JobParametersResolver jobParametersResolver;

    /**
     * @return list of jobs at the summary level
     */
    public List<JenkinsJob> getJobs() {
        Map<String, Job> jobs = jenkinsServer.getJobs();
        return mapJobs(jobs);
    }

    /**
     * @param folder folder
     * @return list of jobs in folder
     */
    public List<JenkinsJob> getJobsInFolder(String folder) {
        JobWithDetails job = jenkinsServer.getJobByName(folder);
        Map<String, Job> jobs = jenkinsServer.getJobsByFolder(folder, job.getUrl());
        return mapJobs(job, jobs);
    }

    /**
     *
     * @param jobName job name
     * @return last build of job
     */
    @SneakyThrows
    public BuildWithDetails getLastBuild(String jobName){
        JobWithDetails job = jenkinsServer.getJobByName(jobName);
        return job.getLastBuild().details();
    }

    /**
     * Runs job with name {@code jobName} and wait until its finished
     *
     * @param jobToRun       Jenkins job
     * @return build details
     */
    public BuildWithDetails runJob(JobToRun jobToRun) {
        var jobName = jobToRun.getJobName();
        var jobParameters = jobToRun.getParameters();

        JobWithDetailsWithProperties job = jenkinsServer.getJobByNameWithProperties(jobName);
        Map<String, String> params = jobParametersResolver.resolve(job, jobParameters);
        var queueReference = buildJob(job, jobName, params);

        jenkinsWaitingService.waitUntilJobInQueue(jobName, queueReference);

        jenkinsWaitingService.waitUntilJobNotStarted(jobName, queueReference);

        var queueItem = jenkinsServer.getQueueItem(queueReference);

        if (queueItem.isCancelled()) {
            var build = jenkinsServer.getBuild(queueItem);
            return getDetails(jobName, build);
        }

        var build = jenkinsServer.getBuild(queueItem);
        var continuousBuild = new ContinuousBuild(jobToRun.getUserId(), jobName, build);
        jenkinsWaitingService.waitUntilJobIsBuilding(continuousBuild);
        return getDetails(jobName, build);
    }

    private List<JenkinsJob> mapJobs(Map<String, Job> jobs) {
        return jobs.values().stream()
            .map(JenkinsJob::new)
            .collect(toList());
    }

    private List<JenkinsJob> mapJobs(JobWithDetails rootJob, Map<String, Job> jobs) {
        return jobs.values().stream()
                .map(job -> new JenkinsJob(job, rootJob.getFullName() + "/" + job.getName()))
                .collect(toList());
    }

    private QueueReference buildJob(JobWithDetails job, String jobName, Map<String, String> params) {
        try {
            if (params.isEmpty()) {
                return job.build(true);
            }
            return job.build(params, true);
        } catch (HttpResponseException e) {
            log.error(e.getMessage(), e);
            throw new RunJobJenkinsApiException("Unable to run job " + jobName + " : " + e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new RunJobJenkinsApiException("Unable to run job " + jobName);
        }
    }

    private static BuildWithDetails getDetails(String jobName, Build build) {
        try {
            return build.details();
        } catch (IOException e) {
            throw new BuildDetailsNotFoundJenkinsApiException("Unable to find details for job " + jobName +
                " and build N " + build.getNumber(), e);
        }
    }
}
