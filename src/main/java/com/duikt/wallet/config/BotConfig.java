package com.duikt.wallet.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class BotConfig {

    @Value("${telegram.bot.username}")
    private String botUserName;

    @Value("${telegram.bot.token}")
    private String botToken;
}
