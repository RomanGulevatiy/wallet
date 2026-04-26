package com.duikt.wallet.bot;

import com.duikt.wallet.config.BotConfig;
import com.duikt.wallet.dto.UserDto;
import com.duikt.wallet.exception.UserNotFoundException;
import com.duikt.wallet.exception.WalletException;
import com.duikt.wallet.service.BankService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
public class WalletTelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String botToken;
    @Getter private final String botUserName;

    private final TelegramClient telegramClient;
    private final BankService bankService;

    public WalletTelegramBot(BotConfig config, BankService bankService) {
        this.botToken = config.getBotToken();
        this.botUserName = config.getBotUserName();
        this.telegramClient = new OkHttpTelegramClient(getBotToken());
        this.bankService = bankService;
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

        if(!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        var text = update.getMessage().getText().trim();
        var chatId = update.getMessage().getChatId();

        log.info("Received message from chatId={} with text='{}'", chatId, text);

        String[] parts = text.split("\\s+");
        String command = parts[0].toLowerCase();

        String response = switch(command) {
            case "/start"   -> handleStart();
            case "/help"    -> handleHelp();
            case "/users"   -> handleUsers();
            case "/profile" -> handleProfile(parts);
            case "/deposit" -> handleDeposit(parts);
            case "/withdraw"-> handleWithdraw(parts);
            default -> handleUnknow();
        };

        sendMessage(chatId, response);
    }

    // RESPONSES

    private String handleUnknow() {
        return """
                Unknown command.
                Please use /help to begin.
                """;
    }

    private String handleStart() {
        return String.format("""
                Welcome to the %s!
                
                Here you can manage wallets, check balances, and perform transactions.
                Use /help to see available commands.
                """,
                getBotUserName());
    }

    private String handleHelp() {
        return """
                Available commands:
                * /start - Start the bot and see the welcome message
                * /help - Show this help message
                
                * /users - List all users and their balances
                * /profile <id> - Info about user
                * /deposit <id> <amount> - Deposit money to user wallet
                * /withdraw <id> <amount> - Withdraw money from user wallet
                """;
    }

    private String handleUsers() {
        List<UserDto> users = bankService.getAllUsers();

        if(users.isEmpty()) {
            return "No users found";
        }

        StringBuilder sb = new StringBuilder("Users:\n");
        for(UserDto user : users) {
            sb.append(
                    String.format("* Id: %d | %s | Balance: %.2f$\n",
                    user.getId(), user.getName(), user.getBalance())
            );
        }
        return sb.toString();
    }

    private String handleProfile(String[] parts) {

        if(parts.length < 2) {
            return "Please provide a user id. Usage: /profile <id>";
        }

        try {
            Long userId = Long.parseLong(parts[1]);
            UserDto user = bankService.getUserDtoById(userId);

            return String.format("""
                    %2$s profile
                    * Id: %d
                    * Name: %s
                    * Balance: %.2f$
                    """,
                    user.getId(), user.getName(), user.getBalance());
        }
        catch(NumberFormatException ex) {
            return "Invalid user id format. Please provide a numeric id. Usage: /profile <id>";
        }
        catch(UserNotFoundException ex) {
            return "User not found. Please check the id and try again.";
        }
    }

    private String handleDeposit(String[] parts) {

        if(parts.length < 3) {
            return "Please provide a user id and an amount. Usage: /deposit <id> <amount>";
        }

        try {
            Long userID = Long.parseLong(parts[1]);
            BigDecimal amount = new BigDecimal(parts[2]);

            bankService.deposit(userID, amount);

            UserDto user = bankService.getUserDtoById(userID);
            return String.format("""
                    [ + ] %.2f$ deposited to %s's wallet.
                    New balance: %.2f$
                    """,
                    amount, user.getName(), user.getBalance());
        }
        catch(NumberFormatException ex) {
            return "Invalid format. Please provide a numeric user id and amount. Usage: /deposit <id> <amount>";
        }
        catch(UserNotFoundException ex) {
            return "User not found. Please check the id and try again.";
        }
        catch(WalletException ex) {
            return "Failed to deposit: " + ex.getMessage();
        }
    }

    private String handleWithdraw(String[] parts) {

        if(parts.length < 3) {
            return "Please provide a user id and an amount. Usage: /withdraw <id> <amount>";
        }

        try {
            Long userID = Long.parseLong(parts[1]);
            BigDecimal amount = new BigDecimal(parts[2]);

            bankService.withdraw(userID, amount);

            UserDto userDto = bankService.getUserDtoById(userID);
            return String.format("""
                    [ - ] %.2f$ withdrawn from %s's wallet.
                    New balance: %.2f$
                    """,
                    amount, userDto.getName(), userDto.getBalance());
        }
        catch(NumberFormatException ex) {
            return "Invalid format. Please provide a numeric user id and amount. Usage: /withdraw <id> <amount>";
        }
        catch(UserNotFoundException ex) {
            return "User not found. Please check the id and try again.";
        }
        catch(WalletException ex) {
            return "Failed to withdraw: " + ex.getMessage();
        }
    }

    // HELPER METHODS

    private void sendMessage(Long chatId, String text) {
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
