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
@Order(99)
public class UnknownCommand implements Command {

    private final CommandMessageSender messageSender;

    @Override
    public boolean canHandle(Update update) {
        return CommandUtils.hasText(update);
    }

    @Override
    public void handle(Update update) {
        Long chatId = CommandUtils.getChatId(update);
        if(chatId == null) {
            return;
        }

        String response = """
                Unknown command.
                Please use /help to begin.
                """;
        messageSender.send(chatId, response);
    }

    @Override
    public String getCommand() {
        return CommandName.UNKNOWN.getName();
    }
}
