package com.duikt.wallet.bot;

import com.duikt.wallet.command.CommandHandler;
import com.duikt.wallet.config.BotConfig;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class WalletTelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final CommandHandler commandHandler;
    private final String botToken;

    public WalletTelegramBot(BotConfig config, CommandHandler commandHandler) {
        this.commandHandler = commandHandler;
        this.botToken = config.getBotToken();
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        commandHandler.handle(update);
    }
}
