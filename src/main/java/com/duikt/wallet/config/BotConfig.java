package com.duikt.wallet.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class BotConfig {

    @Value("${telegram.bot.username}")
    private String botUserName;

    @Value("${telegram.bot.token}")
    private String BotToken;
}
