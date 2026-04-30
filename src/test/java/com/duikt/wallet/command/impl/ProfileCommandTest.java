package com.duikt.wallet.command.impl;

import com.duikt.wallet.command.CommandName;
import com.duikt.wallet.dto.UserDto;
import com.duikt.wallet.exception.UserNotFoundException;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileCommandTest {

    @Mock
    private BankService bankService;

    @Mock
    private CommandMessageSender messageSender;

    @InjectMocks
    private ProfileCommand profileCommand;

    @Test
    @DisplayName("Prompts for user id when missing")
    void promptsForUserIdWhenMissing() {
        Update update = buildUpdate(CommandName.PROFILE.getName(), 20L);

        profileCommand.handle(update);

        verify(messageSender).send(20L, "Please provide a user id. Usage: /profile <id>");
    }

    @Test
    @DisplayName("Rejects non-numeric user id")
    void rejectsNonNumericUserId() {
        Update update = buildUpdate("/profile abc", 20L);

        profileCommand.handle(update);

        verify(messageSender).send(20L, "Invalid user id format. Please provide a numeric id. Usage: /profile <id>");
    }

    @Test
    @DisplayName("Returns not found message when user does not exist")
    void returnsNotFoundMessageWhenUserDoesNotExist() {
        Update update = buildUpdate("/profile 99", 20L);
        when(bankService.getUserDtoById(99L)).thenThrow(new UserNotFoundException("User not found"));

        profileCommand.handle(update);

        verify(messageSender).send(20L, "User not found. Please check the id and try again.");
    }

    @Test
    @DisplayName("Sends profile details when user exists")
    void sendsProfileDetailsWhenUserExists() {
        Update update = buildUpdate("/profile 7", 20L);
        UserDto user = UserDto.builder()
                .id(7L)
                .name("Alice")
                .balance(new BigDecimal("42.10"))
                .build();
        when(bankService.getUserDtoById(7L)).thenReturn(user);

        profileCommand.handle(update);

        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageSender).send(org.mockito.ArgumentMatchers.eq(20L), textCaptor.capture());
        assertThat(textCaptor.getValue())
                .contains("Alice profile")
                .contains("* Id: 7")
                .contains("* Name: Alice")
                .contains("* Balance: 42.10$");
    }

    @Test
    @DisplayName("Does not send profile details when chat id is missing")
    void doesNotSendProfileDetailsWhenChatIdMissing() {
        Update update = new Update();

        profileCommand.handle(update);

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




