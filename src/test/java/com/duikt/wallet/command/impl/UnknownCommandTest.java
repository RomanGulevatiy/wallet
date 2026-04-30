package com.duikt.wallet.command.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class UnknownCommandTest {

    @Mock
    private CommandMessageSender messageSender;

    @InjectMocks
    private UnknownCommand unknownCommand;

    @Test
    @DisplayName("Sends unknown command response for text updates")
    void sendsUnknownCommandResponseForTextUpdates() {
        Update update = buildUpdate("/unknown", 44L);

        unknownCommand.handle(update);

        verify(messageSender).send(44L, """
                Unknown command.
                Please use /help to begin.
                """);
    }

    @Test
    @DisplayName("Does not send unknown response when chat id is missing")
    void doesNotSendUnknownResponseWhenChatIdMissing() {
        Update update = new Update();

        unknownCommand.handle(update);

        verifyNoInteractions(messageSender);
    }

    private Update buildUpdate(String text, Long chatId) {
        Message message = new Message();
        if(chatId != null) {
            message.setChat(new Chat(chatId, "private"));
        }
        message.setText(text);

        Update update = new Update();
        update.setMessage(message);
        return update;
    }
}



