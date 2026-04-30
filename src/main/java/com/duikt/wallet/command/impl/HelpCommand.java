package com.duikt.wallet.command.impl;

import com.duikt.wallet.command.Command;
import com.duikt.wallet.command.CommandName;
import com.duikt.wallet.command.CommandUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Order(2)
public class HelpCommand implements Command {

    private final CommandMessageSender messageSender;

    @Override
    public boolean canHandle(Update update) {
        return CommandUtils.isCommand(update, CommandName.HELP.getName());
    }

    @Override
    public void handle(Update update) {
        Long chatId = CommandUtils.getChatId(update);
        if(chatId == null) {
            return;
        }

        String response = """
                Available commands:
                * /start - Start the bot and see the welcome message
                * /help - Show this help message

                * /users - List all users and their balances
                * /profile <id> - Info about user
                * /deposit <id> <amount> - Deposit money to user wallet
                * /withdraw <id> <amount> - Withdraw money from user wallet
                """;
        messageSender.send(chatId, response);
    }

    @Override
    public String getCommand() {
        return CommandName.HELP.getName();
    }
}
