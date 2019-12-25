package com.github.mikesafonov.jenkins.telegram.chatops.jenkins;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.TelegramBotSender;
import com.github.mikesafonov.jenkins.telegram.chatops.dto.JobToRun;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * In-memory queue of Jenkins jobs to run
 *
 * @author Mike Safonov
 */
@Service
@RequiredArgsConstructor
public class JobRunQueueService {

    private final JenkinsService jenkinsService;
    private final TelegramBotSender telegramBotSender;
    private BlockingQueue<JobToRun> jobs = new LinkedBlockingQueue<>();

    /**
     * add new job to queue
     *
     * @param jobToRun Jenkins job
     */
    public void registerJob(JobToRun jobToRun) {
        jobs.add(jobToRun);
    }

    @Scheduled(fixedDelay = 1000)
    public void runJobs() {
        getJobsToRun().forEach(job -> {
            try {
                var build = jenkinsService.runJob(job.getJobName());
                var message = "Build of *" + job.getJobName() + "* has been finished\nResult: *"
                        + build.getResult() + "*\n[Launch on Jenkins](" + build.getUrl() + ")";
                telegramBotSender.sendMarkdownTextMessage(job.getUserId(), message);
            } catch (Exception e) {
                telegramBotSender.sendMarkdownTextMessage(job.getUserId(),
                        "Exception when running job *" + job.getJobName() + "*:\n" + e.getMessage());
            }
        });
    }

    /**
     * @return all current jobs from {@code jobs} queue.
     */
    private List<JobToRun> getJobsToRun() {
        var jobToRuns = new ArrayList<JobToRun>();
        jobs.drainTo(jobToRuns);
        return jobToRuns;
    }
}
