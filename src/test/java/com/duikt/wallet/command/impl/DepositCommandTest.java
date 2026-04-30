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
class DepositCommandTest {

    @Mock
    private BankService bankService;

    @Mock
    private CommandMessageSender messageSender;

    @InjectMocks
    private DepositCommand depositCommand;

    @Test
    @DisplayName("Prompts for user id and amount when arguments are missing")
    void promptsForUserIdAndAmountWhenArgumentsMissing() {
        Update update = buildUpdate(CommandName.DEPOSIT.getName(), 30L);

        depositCommand.handle(update);

        verify(messageSender).send(30L, "Please provide a user id and an amount. Usage: /deposit <id> <amount>");
    }

    @Test
    @DisplayName("Rejects invalid numeric format")
    void rejectsInvalidNumericFormat() {
        Update update = buildUpdate("/deposit x y", 30L);

        depositCommand.handle(update);

        verify(messageSender).send(30L, "Invalid format. Please provide a numeric user id and amount. Usage: /deposit <id> <amount>");
    }

    @Test
    @DisplayName("Returns not found message when user does not exist")
    void returnsNotFoundMessageWhenUserDoesNotExist() {
        Update update = buildUpdate("/deposit 7 10", 30L);
        doThrow(new UserNotFoundException("User not found"))
                .when(bankService)
                .deposit(7L, new BigDecimal("10"));

        depositCommand.handle(update);

        verify(messageSender).send(30L, "User not found. Please check the id and try again.");
    }

    @Test
    @DisplayName("Returns wallet error message when deposit fails")
    void returnsWalletErrorMessageWhenDepositFails() {
        Update update = buildUpdate("/deposit 7 10", 30L);
        doThrow(new WalletException("Amount must be greater than zero"))
                .when(bankService)
                .deposit(7L, new BigDecimal("10"));

        depositCommand.handle(update);

        verify(messageSender).send(30L, "Failed to deposit: Amount must be greater than zero");
    }

    @Test
    @DisplayName("Sends confirmation when deposit succeeds")
    void sendsConfirmationWhenDepositSucceeds() {
        Update update = buildUpdate("/deposit 7 10.50", 30L);
        UserDto user = UserDto.builder()
                .id(7L)
                .name("Alice")
                .balance(new BigDecimal("20.50"))
                .build();
        when(bankService.getUserDtoById(7L)).thenReturn(user);

        depositCommand.handle(update);

        verify(bankService).deposit(7L, new BigDecimal("10.50"));
        ArgumentCaptor<String> textCaptor = ArgumentCaptor.forClass(String.class);
        verify(messageSender).send(org.mockito.ArgumentMatchers.eq(30L), textCaptor.capture());
        assertThat(textCaptor.getValue())
                .contains("[ + ] 10.50$")
                .contains("Alice's wallet")
                .contains("New balance: 20.50$");
    }

    @Test
    @DisplayName("Does not send deposit response when chat id is missing")
    void doesNotSendDepositResponseWhenChatIdMissing() {
        Update update = new Update();

        depositCommand.handle(update);

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



