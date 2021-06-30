package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.offbytwo.jenkins.model.Job;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;

/**
 * @author Mike Safonov
 */
@Log4j2
@AllArgsConstructor
public class JenkinsJob {
    private final Job originalJob;
    @Getter
    private final String fullName;


    public JenkinsJob(Job originalJob) {
        this(originalJob, (originalJob.getFullName() == null) ? originalJob.getName() : originalJob.getFullName());
    }

    public boolean isFolder() {
        return JenkinsServerWrapper.FOLDER_CLASS.equals(originalJob.get_class())
                || JenkinsServerWrapper.WORKFLOW_MULTIBRANCH_PROJECT_CLASS.equals(originalJob.get_class())
                || JenkinsServerWrapper.BRANCH_FOLDER_CLASS.equals(originalJob.get_class());
    }


    public Job getOriginalJob() {
        return originalJob;
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
