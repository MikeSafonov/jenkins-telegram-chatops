package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.JenkinsInstanceProperties;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.FolderJob;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Mike Safonov
 */
@Log4j2
@Component
public class JenkinsServerWrapper {
    private static final String FOLDER_CLASS = "com.cloudbees.hudson.plugins.folder.Folder";

    private final JenkinsServer jenkinsServer;

    public JenkinsServerWrapper(JenkinsInstanceProperties jenkinsInstanceProperties) {
        try {
            jenkinsServer = new JenkinsServer(
                    new URI(jenkinsInstanceProperties.getUrl()),
                    jenkinsInstanceProperties.getUsername(),
                    jenkinsInstanceProperties.getToken()
            );
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Job> getBuildableJobs() {
        List<Job> buildableJobs = new ArrayList<>();
        try {
            Map<String, Job> jobs = jenkinsServer.getJobs();
            collectBuildableJobsFromMap(jobs, buildableJobs);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return buildableJobs;
    }

    private void collectBuildableJobsFromMap(Map<String, Job> jobs, List<Job> collectList) {
        jobs.forEach((s, job) -> {
            try {
                if (FOLDER_CLASS.equals(job.get_class())) {
                    FolderJob folderJob = new FolderJob(job.getName(), job.getUrl());
                    collectBuildableJobsFromMap(jenkinsServer.getJobs(folderJob), collectList);
                } else {
                    JobWithDetails details = job.details();
                    if(details.isBuildable()){
                        collectList.add(job);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        });
    }
}
