package com.duikt.wallet.command.impl;

import com.duikt.wallet.command.CommandName;
import com.duikt.wallet.dto.UserDto;
import com.duikt.wallet.exception.UserNotFoundException;
import com.duikt.wallet.exception.WalletException;
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
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WithdrawCommandTest {

    @Mock
    private BankService bankService;

    @Mock
    private CommandMessageSender messageSender;

    @InjectMocks
    private WithdrawCommand withdrawCommand;

    @Test
    @DisplayName("Prompts for user id and amount when arguments are missing")
    void promptsForUserIdAndAmountWhenArgumentsMissing() {
        Update update = buildUpdate(CommandName.WITHDRAW.getName(), 31L);

        withdrawCommand.handle(update);

        verify(messageSender).send(31L, "Please provide a user id and an amount. Usage: /withdraw <id> <amount>");
    }

    @Test
    @DisplayName("Rejects invalid numeric format")
    void rejectsInvalidNumericFormat() {
        Update update = buildUpdate("/withdraw x y", 31L);

        withdrawCommand.handle(update);

        verify(messageSender).send(31L, "Invalid format. Please provide a numeric user id and amount. Usage: /withdraw <id> <amount>");
    }

    @Test
    @DisplayName("Returns not found message when user does not exist")
    void returnsNotFoundMessageWhenUserDoesNotExist() {
        Update update = buildUpdate("/withdraw 7 10", 31L);
        doThrow(new UserNotFoundException("User not found"))
                .when(bankService)
                .withdraw(7L, new BigDecimal("10"));

        withdrawCommand.handle(update);

        verify(messageSender).send(31L, "User not found. Please check the id and try again.");
    }

    @Test
    @DisplayName("Returns wallet error message when withdraw fails")
    void returnsWalletErrorMessageWhenWithdrawFails() {
        Update update = buildUpdate("/withdraw 7 10", 31L);
        doThrow(new WalletException("Insufficient balance"))
                .when(bankService)
                .withdraw(7L, new BigDecimal("10"));

        withdrawCommand.handle(update);

        verify(messageSender).send(31L, "Failed to withdraw: Insufficient balance");
    }

    @Test
    @DisplayName("Sends confirmation when withdraw succeeds")
    void sendsConfirmationWhenWithdrawSucceeds() {
        Update update = buildUpdate("/withdraw 7 10.50", 31L);
        UserDto user = UserDto.builder()
                .id(7L)
                .name("Alice")
                .balance(new BigDecimal("20.50"))
                .build();
        when(bankService.getUserDtoById(7L)).thenReturn(user);

        withdrawCommand.handle(update);

        verify(bankService).withdraw(7L, new BigDecimal("10.50"));
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageSender).send(org.mockito.ArgumentMatchers.eq(31L), textCaptor.capture());
        assertThat(textCaptor.getValue())
                .contains("[ - ] 10.50$")
                .contains("Alice's wallet")
                .contains("New balance: 20.50$");
    }

    @Test
    @DisplayName("Does not send withdraw response when chat id is missing")
    void doesNotSendWithdrawResponseWhenChatIdMissing() {
        Update update = new Update();

        withdrawCommand.handle(update);

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




