package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.offbytwo.jenkins.model.Job;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * @author Mike Safonov
 */
@Log4j2
@Service
@RequiredArgsConstructor
public class JenkinsService {
    private final JenkinsServerWrapper jenkinsServer;

    public void printJobs() {
        List<Job> buildableJobs = jenkinsServer.getBuildableJobs();
        buildableJobs.forEach(job -> {
            log.info(job.getFullName() + "---" + job.getName() + " " + job.get_class());
        });
    }
}
