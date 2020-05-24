package com.github.mikesafonov.jenkins.telegram.chatops.bot.commands;

import lombok.Builder;
import lombok.Getter;

import java.util.function.Consumer;

/**
 * @author Mike Safonov
 */
@Builder
@Getter
public class Command {

    private UpdateCheck command;
    private boolean authorized;
    private int argsCount;
    private Consumer<CommandContext> action;

    public static EqualsUpdateCheck equals(String value) {
        return new EqualsUpdateCheck(value);
    }

    public static StartsWithUpdateCheck startsWith(String value) {
        return new StartsWithUpdateCheck(value);
    }

    public static class CommandBuilder {

        public CommandBuilder authorized() {
            this.authorized = true;
            return this;
        }

        public CommandBuilder unauthorized(){
            this.authorized = false;
            return this;
        }
    }
}
