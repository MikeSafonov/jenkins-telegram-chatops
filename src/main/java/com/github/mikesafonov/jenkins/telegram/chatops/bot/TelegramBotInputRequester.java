package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.ContinuousBuild;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.InMemoryInputParameterDataStorage;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.InputParameterWithValue;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.PendingInput;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.inputs.ChoiceInputParameter;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.inputs.InputParameter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Mike Safonov
 */
@Service
@RequiredArgsConstructor
public class TelegramBotInputRequester {
    private final TelegramBotSender sender;
    private final UserStateService userStateService;
    private final InMemoryInputParameterDataStorage dataStorage;

    public void request(ContinuousBuild continuousBuild, List<PendingInput> pendingInputs) {
        var notRequestedInputs = continuousBuild.getNotRequestedInputs(pendingInputs);
        var chatId = continuousBuild.getChatId().toString();
        for (var input : notRequestedInputs) {
            continuousBuild.addRequestedInput(input);
            sender.sendTelegramMessage(SendMessage.builder()
                    .chatId(chatId)
                    .text("Build requested input:\n" + input.getMessage())
                    .build());
            for (var parameter : input.getInputs()) {
                if (parameter instanceof ChoiceInputParameter) {
                    var choiceParameter = (ChoiceInputParameter) parameter;
                    sender.sendTelegramMessage(
                            SendMessage.builder()
                                    .chatId(chatId)
                                    .text("Parameter *" + choiceParameter.getName() + "*:")
                                    .parseMode(ParseMode.MARKDOWN)
                                    .replyMarkup(buildReplyKeyboard(continuousBuild, input, choiceParameter))
                                    .build()
                    );
                } else {
                    sender.sendTelegramMessage(
                            SendMessage.builder()
                                    .chatId(chatId)
                                    .text("Unknown parameter *" + parameter.getName() + "*:")
                                    .parseMode(ParseMode.MARKDOWN)
                                    .build());
                }
            }
        }
        if (!notRequestedInputs.isEmpty()) {
            userStateService.update(continuousBuild.getChatId(), UserState.WAIT_INPUTS);
        }
    }

    private ReplyKeyboard buildReplyKeyboard(ContinuousBuild continuousBuild, PendingInput pendingInput,
                                             ChoiceInputParameter choiceParameter) {
        var choices = choiceParameter.getDefinition().getChoices().stream()
                .map(s -> InlineKeyboardButton.builder()
                        .text(s)
                        .callbackData(createCallbackInputParameter(continuousBuild, pendingInput, choiceParameter, s))
                        .build())
                .map(List::of)
                .collect(Collectors.toList());
        return InlineKeyboardMarkup.builder()
                .keyboard(choices)
                .build();
    }


    @SneakyThrows
    private String createCallbackInputParameter(ContinuousBuild continuousBuild, PendingInput pendingInput,
                                                InputParameter<?> parameter,
                                                String data) {
        return dataStorage.put(new InputParameterWithValue(continuousBuild.getBuild(),
                pendingInput,
                parameter.getName(), data));
    }
}
