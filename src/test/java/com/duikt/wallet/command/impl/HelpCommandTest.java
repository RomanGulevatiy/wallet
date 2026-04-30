package com.duikt.wallet.command.impl;

import com.duikt.wallet.command.CommandName;
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
class HelpCommandTest {

    @Mock
    private CommandMessageSender messageSender;

    @InjectMocks
    private HelpCommand helpCommand;

    @Test
    @DisplayName("Sends help text for help command")
    void sendsHelpTextForHelpCommand() {
        Update update = buildUpdate(CommandName.HELP.getName(), 11L);

        helpCommand.handle(update);

        verify(messageSender).send(11L, """
                Available commands:
                * /start - Start the bot and see the welcome message
                * /help - Show this help message

                * /users - List all users and their balances
                * /profile <id> - Info about user
                * /deposit <id> <amount> - Deposit money to user wallet
                * /withdraw <id> <amount> - Withdraw money from user wallet
                """);
    }

    @Test
    @DisplayName("Does not send help text when chat id is missing")
    void doesNotSendHelpTextWhenChatIdMissing() {
        Update update = new Update();

        helpCommand.handle(update);

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




