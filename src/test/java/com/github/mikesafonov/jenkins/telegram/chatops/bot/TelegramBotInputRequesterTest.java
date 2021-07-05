package com.github.mikesafonov.jenkins.telegram.chatops.bot;

import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.ContinuousBuild;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.InMemoryInputParameterDataStorage;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.InputParameterWithValue;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.PendingInput;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.inputs.ChoiceInputParameter;
import com.github.mikesafonov.jenkins.telegram.chatops.jenkins.model.inputs.ChoiceInputParameterDefinition;
import com.offbytwo.jenkins.model.Build;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author Mike Safonov
 */
class TelegramBotInputRequesterTest {
    private TelegramBotSender sender;
    private UserStateService userStateService;
    private InMemoryInputParameterDataStorage dataStorage;
    private Build build;
    private ContinuousBuild continuousBuild;
    private TelegramBotInputRequester inputRequester;

    @BeforeEach
    void setUp() {
        sender = mock(TelegramBotSender.class);
        userStateService = mock(UserStateService.class);
        dataStorage = mock(InMemoryInputParameterDataStorage.class);
        continuousBuild = mock(ContinuousBuild.class);
        build = mock(Build.class);
        when(continuousBuild.getBuild()).thenReturn(build);
        inputRequester = new TelegramBotInputRequester(sender, userStateService, dataStorage);
    }

    @Nested
    class WhenNotRequestInputsEmpty {
        private List<PendingInput> pendingInputs;

        @BeforeEach
        void setUp() {
            var second = new PendingInput();
            second.setId("2");
            pendingInputs = List.of(second);
            when(continuousBuild.getNotRequestedInputs(pendingInputs)).thenReturn(List.of());
        }

        @Test
        void shouldNotChangeState() {
            inputRequester.request(continuousBuild, pendingInputs);

            verifyNoInteractions(userStateService);
        }
    }

    @Nested
    class WhenRequestInputsNotEmpty {
        private Long chatId;
        private PendingInput pendingInput;
        private List<PendingInput> pendingInputs;

        @BeforeEach
        void setUp() {
            chatId = 10L;
            var choiceInputParameter = new ChoiceInputParameter();
            choiceInputParameter.setName("name");
            var definition = new ChoiceInputParameterDefinition();
            choiceInputParameter.setDefinition(definition);
            definition.setChoices(List.of("one", "two"));
            pendingInput = new PendingInput();
            pendingInput.setId("2");
            pendingInput.setMessage("message");
            pendingInput.setInputs(List.of(choiceInputParameter));
            pendingInputs = List.of(pendingInput);

            when(continuousBuild.getNotRequestedInputs(pendingInputs)).thenReturn(pendingInputs);
            when(continuousBuild.getChatId()).thenReturn(chatId);
            when(dataStorage.put(new InputParameterWithValue(build, pendingInput, "name", "one")))
                    .thenReturn("one");
            when(dataStorage.put(new InputParameterWithValue(build, pendingInput, "name", "two")))
                    .thenReturn("two");
        }

        @Test
        void shouldSendRequestMessage() {
            inputRequester.request(continuousBuild, pendingInputs);

            var captor = ArgumentCaptor.forClass(SendMessage.class);
            verify(sender, times(2)).sendTelegramMessage(captor.capture());
            var message = captor.getAllValues().get(0);
            assertThat(message).satisfies(sendMessage -> {
                assertThat(sendMessage.getChatId()).isEqualTo(chatId.toString());
                assertThat(sendMessage.getText()).isEqualTo("Build requested input:\nmessage");
            });

            verify(continuousBuild).addRequestedInput(pendingInput);
        }

        @Test
        void shouldSendChoicesMessage() {
            inputRequester.request(continuousBuild, pendingInputs);


            var captor = ArgumentCaptor.forClass(SendMessage.class);
            verify(sender, times(2)).sendTelegramMessage(captor.capture());
            var message = captor.getAllValues().get(1);
            assertThat(message).satisfies(sendMessage -> {
                assertThat(sendMessage.getChatId()).isEqualTo(chatId.toString());
                assertThat(sendMessage.getText()).isEqualTo("Parameter *name*:");
                assertThat(sendMessage.getParseMode()).isEqualTo(ParseMode.MARKDOWN);
                var choices = List.of("one", "two").stream()
                        .map(s -> InlineKeyboardButton.builder()
                                .text(s)
                                .callbackData(s)
                                .build()
                        )
                        .map(List::of)
                        .collect(Collectors.toList());

                var expectedKeyboard = InlineKeyboardMarkup.builder()
                        .keyboard(choices)
                        .build();
                assertThat(sendMessage.getReplyMarkup()).isEqualTo(expectedKeyboard);
            });

            verify(continuousBuild).addRequestedInput(pendingInput);
        }

        @Test
        void shouldAddRequestedInput() {
            inputRequester.request(continuousBuild, pendingInputs);

            verify(continuousBuild).addRequestedInput(pendingInput);
        }

        @Test
        void shouldChangeState() {
            inputRequester.request(continuousBuild, pendingInputs);

            verify(userStateService).update(chatId, UserState.WAIT_INPUTS);
        }
    }

}
