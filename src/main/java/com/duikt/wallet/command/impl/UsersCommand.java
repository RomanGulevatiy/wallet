package com.duikt.wallet.command.impl;

import com.duikt.wallet.command.Command;
import com.duikt.wallet.command.CommandName;
import com.duikt.wallet.command.CommandUtils;
import com.duikt.wallet.dto.UserDto;
import com.duikt.wallet.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(3)
public class UsersCommand implements Command {

    private final BankService bankService;
    private final CommandMessageSender messageSender;

    @Override
    public boolean canHandle(Update update) {
        return CommandUtils.isCommand(update, CommandName.USERS.getName());
    }

    @Override
    public void handle(Update update) {
        Long chatId = CommandUtils.getChatId(update);
        if(chatId == null) {
            return;
        }

        List<UserDto> users = bankService.getAllUsers();
        if(users.isEmpty()) {
            messageSender.send(chatId, "No users found");
            return;
        }

        StringBuilder sb = new StringBuilder("Users:\n");
        for(UserDto user : users) {
            sb.append(String.format("* Id: %d | %s | Balance: %.2f$\n",
                    user.getId(), user.getName(), user.getBalance()));
        }
        messageSender.send(chatId, sb.toString());
    }

    @Override
    public String getCommand() {
        return CommandName.USERS.getName();
    }
}
