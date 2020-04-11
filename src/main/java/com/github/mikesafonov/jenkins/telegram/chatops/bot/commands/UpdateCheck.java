package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * @author Mike Safonov
 */
@FunctionalInterface
public interface UpdateCheck {

    boolean support(Update update);
}
