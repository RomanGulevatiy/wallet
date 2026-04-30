package com.duikt.wallet.command.impl;

import com.duikt.wallet.command.CommandName;
import com.duikt.wallet.config.BotConfig;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StartCommandTest {

    @Mock
    private BotConfig config;

    @Mock
    private CommandMessageSender messageSender;

    @InjectMocks
    private StartCommand startCommand;

    @Test
    @DisplayName("Handles the start command and sends the welcome message")
    void handlesStartCommandAndSendsWelcomeMessage() {
        Update update = buildUpdate(CommandName.START.getName(), 10L);
        when(config.getBotUserName()).thenReturn("WalletBot");

        startCommand.handle(update);

        verify(messageSender).send(10L, """
                Welcome to the WalletBot!

                Here you can manage wallets, check balances, and perform transactions.
                Use /help to see available commands.
                """);
    }

    @Test
    @DisplayName("Does not send a message when chat id is missing")
    void doesNotSendMessageWhenChatIdMissing() {
        Update update = new Update();

        startCommand.handle(update);

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



