package com.duikt.wallet.command;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommandHandler {

    private final List<Command> commands;

    public void handle(Update update) {

        for(Command command : commands) {
            if(command.canHandle(update)) {
                command.handle(update);
                return;
            }
        }
    }
}
