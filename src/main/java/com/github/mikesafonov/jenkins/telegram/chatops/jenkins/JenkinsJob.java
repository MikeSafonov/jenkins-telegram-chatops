package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.offbytwo.jenkins.model.Job;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

/**
 * @author Mike Safonov
 */
@Log4j2
public class JenkinsJob {
    private final Job originalJob;

    public JenkinsJob(Job originalJob) {
        this.originalJob = originalJob;
    }

    public boolean isFolder() {
        return JenkinsServerWrapper.FOLDER_CLASS.equals(originalJob.get_class())
                || JenkinsServerWrapper.WORKFLOW_MULTIBRANCH_PROJECT_CLASS.equals(originalJob.get_class());
    }


    public Job getOriginalJob() {
        return originalJob;
    }

    public String getUrl(){
        return originalJob.getUrl();
    }

    public boolean isBuildable() {
        try {
            return originalJob.details().isBuildable();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return false;
        }
    }
}
