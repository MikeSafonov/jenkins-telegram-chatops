package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.BuildNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.JobNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.exceptions.QueueItemNotFoundJenkinsApiException;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.JobWithDetailsWithProperties;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.client.JenkinsHttpClient;
import com.offbytwo.jenkins.client.util.UrlUtils;
import com.offbytwo.jenkins.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @author Mike Safonov
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class JenkinsServerWrapper {
    static final String FOLDER_CLASS = "com.cloudbees.hudson.plugins.folder.Folder";
    static final String BRANCH_FOLDER_CLASS = "jenkins.branch.OrganizationFolder";
    static final String WORKFLOW_MULTIBRANCH_PROJECT_CLASS = "org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject";

    private final JenkinsServer jenkinsServer;
    private final JenkinsHttpClient jenkinsHttpClient;

    /**
     * @return map of jobs at the summary level or empty map if IOException throws
     */
    public Map<String, Job> getJobs() {
        try {
            return jenkinsServer.getJobs();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyMap();
    }

    /**
     * @param folder folder name
     * @param url    folder url
     * @return map of jobs in folder or empty map if IOException throws
     */
    public Map<String, Job> getJobsByFolder(String folder, String url) {
        try {
            return jenkinsServer.getJobs(new FolderJob(folder, url));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return Collections.emptyMap();
    }

    /**
     * @param jobName job`s name
     * @return JobWithDetails of job with name
     */
    public JobWithDetails getJobByName(String jobName) {
        try {
            JobWithDetails job = jenkinsServer.getJob(jobName);
            if (job == null) {
                throw new JobNotFoundJenkinsApiException(jobName);
            }
            return job;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new JobNotFoundJenkinsApiException(jobName, e);
        }
    }

    public JobWithDetailsWithProperties getJobByNameWithProperties(String jobName) {
        try {
            var fullJobPath = UrlUtils.toFullJobPath(jobName);
            return jenkinsHttpClient.get(UrlUtils.toJobBaseUrl(null, fullJobPath),
                    JobWithDetailsWithProperties.class);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new JobNotFoundJenkinsApiException(jobName, e);
        }
    }

    /**
     * @param queueRef queue reference
     * @return queue item
     */
    public QueueItem getQueueItem(QueueReference queueRef) {
        try {
            var queueItem = jenkinsServer.getQueueItem(queueRef);
            if (queueItem == null) {
                throw new QueueItemNotFoundJenkinsApiException(queueRef.getQueueItemUrlPart());
            }
            return queueItem;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new QueueItemNotFoundJenkinsApiException(queueRef.getQueueItemUrlPart(), e);
        }
    }

    /**
     * @param queueItem item in queue
     * @return build
     */
    public Build getBuild(QueueItem queueItem) {
        try {
            var build = jenkinsServer.getBuild(queueItem);
            if (build == null) {
                throw new BuildNotFoundJenkinsApiException(queueItem.getId());
            }
            return build;
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new BuildNotFoundJenkinsApiException(queueItem.getId(), e);
        }
    }
}
