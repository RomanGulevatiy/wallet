package com.duikt.wallet.command.impl;

import com.duikt.wallet.command.Command;
import com.duikt.wallet.command.CommandName;
import com.duikt.wallet.command.CommandUtils;
import com.duikt.wallet.config.BotConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Order(1)
public class StartCommand implements Command {

    private final BotConfig config;
    private final CommandMessageSender messageSender;

    @Override
    public boolean canHandle(Update update) {
        return CommandUtils.isCommand(update, CommandName.START.getName());
    }

    @Override
    public void handle(Update update) {
        Long chatId = CommandUtils.getChatId(update);
        if(chatId == null) {
            return;
        }

        String response = String.format("""
                Welcome to the %s!

                Here you can manage wallets, check balances, and perform transactions.
                Use /help to see available commands.
                """,
                config.getBotUserName());
        messageSender.send(chatId, response);
    }

    @Override
    public String getCommand() {
        return CommandName.START.getName();
    }
}
