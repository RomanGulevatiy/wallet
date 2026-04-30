package com.duikt.wallet.command.impl;

import com.duikt.wallet.command.Command;
import com.duikt.wallet.command.CommandName;
import com.duikt.wallet.command.CommandUtils;
import com.duikt.wallet.dto.UserDto;
import com.duikt.wallet.exception.UserNotFoundException;
import com.duikt.wallet.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Order(4)
public class ProfileCommand implements Command {

    private final BankService bankService;
    private final CommandMessageSender messageSender;

    @Override
    public boolean canHandle(Update update) {
        return CommandUtils.isCommand(update, CommandName.PROFILE.getName());
    }

    @Override
    public void handle(Update update) {
        Long chatId = CommandUtils.getChatId(update);
        if(chatId == null) {
            return;
        }

        String[] parts = CommandUtils.getParts(update);
        if(parts.length < 2) {
            messageSender.send(chatId, "Please provide a user id. Usage: /profile <id>");
            return;
        }

        try {
            Long userId = Long.parseLong(parts[1]);
            UserDto user = bankService.getUserDtoById(userId);

            String response = String.format("""
                    %2$s profile
                    * Id: %d
                    * Name: %s
                    * Balance: %.2f$
                    """,
                    user.getId(), user.getName(), user.getBalance());
            messageSender.send(chatId, response);
        }
        catch(NumberFormatException ex) {
            messageSender.send(chatId, "Invalid user id format. Please provide a numeric id. Usage: /profile <id>");
        }
        catch(UserNotFoundException ex) {
            messageSender.send(chatId, "User not found. Please check the id and try again.");
        }
    }

    @Override
    public String getCommand() {
        return CommandName.PROFILE.getName();
    }
}
