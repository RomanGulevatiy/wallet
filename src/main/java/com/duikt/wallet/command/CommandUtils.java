package com.duikt.wallet.command;

import org.telegram.telegrambots.meta.api.objects.Update;

public final class CommandUtils {

    private CommandUtils() {
    }

    public static boolean hasText(Update update) {
        return update != null
                && update.hasMessage()
                && update.getMessage().hasText();
    }

    public static String getText(Update update) {
        if(!hasText(update)) {
            return null;
        }
        return update.getMessage().getText().trim();
    }

    public static String getCommand(Update update) {
        String text = getText(update);
        if(text == null || text.isEmpty()) {
            return null;
        }
        String[] parts = text.split("\\s+");
        return parts[0].toLowerCase();
    }

    public static String[] getParts(Update update) {
        String text = getText(update);
        if(text == null || text.isEmpty()) {
            return new String[0];
        }
        return text.split("\\s+");
    }

    public static Long getChatId(Update update) {
        if(update == null || !update.hasMessage()) {
            return null;
        }
        return update.getMessage().getChatId();
    }

    public static boolean isCommand(Update update, String command) {
        String actual = getCommand(update);
        return actual != null && actual.equalsIgnoreCase(command);
    }
}
