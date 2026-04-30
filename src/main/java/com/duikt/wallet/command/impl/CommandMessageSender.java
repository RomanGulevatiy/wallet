package com.duikt.wallet.command.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandMessageSender {

    private final TelegramClient telegramClient;

    public void send(Long chatId, String text) {
        if(chatId == null || text == null) {
            return;
        }

        SendMessage msg = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();

        try {
            telegramClient.execute(msg);
        }
        catch(TelegramApiException e) {
            log.error("Failed to send message to chatId={}", chatId, e);
        }
    }
}
