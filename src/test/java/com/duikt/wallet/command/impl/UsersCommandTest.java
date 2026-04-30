package com.duikt.wallet.command.impl;

import com.duikt.wallet.command.CommandName;
import com.duikt.wallet.dto.UserDto;
import com.duikt.wallet.service.BankService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsersCommandTest {

    @Mock
    private BankService bankService;

    @Mock
    private CommandMessageSender messageSender;

    @InjectMocks
    private UsersCommand usersCommand;

    @Test
    @DisplayName("Sends a no-users message when the list is empty")
    void sendsNoUsersMessageWhenListIsEmpty() {
        Update update = buildUpdate(CommandName.USERS.getName(), 12L);
        when(bankService.getAllUsers()).thenReturn(List.of());

        usersCommand.handle(update);

        verify(messageSender).send(12L, "No users found");
    }

    @Test
    @DisplayName("Sends the formatted user list when users exist")
    void sendsFormattedUserListWhenUsersExist() {
        Update update = buildUpdate(CommandName.USERS.getName(), 12L);
        UserDto first = UserDto.builder().id(1L).name("Alice").balance(new BigDecimal("10.00")).build();
        UserDto second = UserDto.builder().id(2L).name("Bob").balance(new BigDecimal("20.50")).build();
        when(bankService.getAllUsers()).thenReturn(List.of(first, second));

        usersCommand.handle(update);

        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageSender).send(org.mockito.ArgumentMatchers.eq(12L), textCaptor.capture());
        assertThat(textCaptor.getValue())
                .contains("Users:")
                .contains("* Id: 1 | Alice | Balance: 10.00$")
                .contains("* Id: 2 | Bob | Balance: 20.50$");
    }

    @Test
    @DisplayName("Does not send users list when chat id is missing")
    void doesNotSendUsersListWhenChatIdMissing() {
        Update update = new Update();

        usersCommand.handle(update);

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




