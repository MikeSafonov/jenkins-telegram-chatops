package com.github.mikesafonov.jenkins.telegram.chatops.bot.actions;

import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserState;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.UserStateService;
import com.github.mikesafonov.jenkins.telegram.chatops.bot.commands.CommandContext;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.InMemoryInputParameterDataStorage;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.JobInputService;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.function.Consumer;

/**
 * @author Mike Safonov
 */
@RequiredArgsConstructor
public class InputParameterAction implements Consumer<CommandContext> {
    private final InMemoryInputParameterDataStorage dataStorage;
    private final JobInputService jobInputService;
    private final UserStateService userStateService;

    @Override
    public void accept(CommandContext commandContext) {
        try {
            var uuid = commandContext.getUpdate().getCallbackQuery().getData();
            var optionalData = dataStorage.pull(uuid);
            if (optionalData.isPresent()) {
                jobInputService.postInput(optionalData.get());
            } else {
                commandContext.getSender().sendTelegramMessage(SendMessage.builder()
                        .chatId(commandContext.getChatId().toString())
                        .text("No data was found")
                        .build());
            }
        } finally {
            userStateService.update(commandContext.getChatId(), UserState.WAIT_COMMAND);
        }
    }
}
