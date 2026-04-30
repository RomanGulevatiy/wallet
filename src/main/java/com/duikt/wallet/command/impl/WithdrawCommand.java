package com.duikt.wallet.command.impl;

import com.duikt.wallet.command.Command;
import com.duikt.wallet.command.CommandName;
import com.duikt.wallet.command.CommandUtils;
import com.duikt.wallet.dto.UserDto;
import com.duikt.wallet.exception.UserNotFoundException;
import com.duikt.wallet.exception.WalletException;
import com.duikt.wallet.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Order(6)
public class WithdrawCommand implements Command {

    private final BankService bankService;
    private final CommandMessageSender messageSender;

    @Override
    public boolean canHandle(Update update) {
        return CommandUtils.isCommand(update, CommandName.WITHDRAW.getName());
    }

    @Override
    public void handle(Update update) {
        Long chatId = CommandUtils.getChatId(update);
        if(chatId == null) {
            return;
        }

        String[] parts = CommandUtils.getParts(update);
        if(parts.length < 3) {
            messageSender.send(chatId, "Please provide a user id and an amount. Usage: /withdraw <id> <amount>");
            return;
        }

        try {
            Long userId = Long.parseLong(parts[1]);
            BigDecimal amount = new BigDecimal(parts[2]);

            bankService.withdraw(userId, amount);

            UserDto user = bankService.getUserDtoById(userId);
            String response = String.format("""
                    [ - ] %.2f$ withdrawn from %s's wallet.
                    New balance: %.2f$
                    """,
                    amount, user.getName(), user.getBalance());
            messageSender.send(chatId, response);
        }
        catch(NumberFormatException ex) {
            messageSender.send(chatId, "Invalid format. Please provide a numeric user id and amount. Usage: /withdraw <id> <amount>");
        }
        catch(UserNotFoundException ex) {
            messageSender.send(chatId, "User not found. Please check the id and try again.");
        }
        catch(WalletException ex) {
            messageSender.send(chatId, "Failed to withdraw: " + ex.getMessage());
        }
    }

    @Override
    public String getCommand() {
        return CommandName.WITHDRAW.getName();
    }
}
