package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.ArgsParserService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobRunQueueService;
import com.github.mikesafonov.jenkins.telegram.chatops.utils.ArrayUtils;

import java.util.Collections;
import java.util.Map;

/**
 * @author Mike Safonov
 */
public class RunJobFromArgsAction extends BaseRunJobAction {

    private final ArgsParserService argsParserService;

    public RunJobFromArgsAction(JobRunQueueService jobRunQueueService,
                                ArgsParserService argsParserService) {
        super(jobRunQueueService);
        this.argsParserService = argsParserService;
    }

    @Override
    public void accept(CommandContext context) {
        String jobName = context.getArgs()[0];
        Map<String, String> parameters = Collections.emptyMap();
        if (context.getArgs().length > 1) {
            parameters = argsParserService.parse(ArrayUtils.copyWithoutFirst(context.getArgs()));
        }
        runJob(jobName, context, parameters);
    }
}
