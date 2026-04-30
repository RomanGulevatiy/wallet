package com.duikt.wallet.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.message.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.assertj.core.api.Assertions.assertThat;

class CommandUtilsTest {

    @Test
    @DisplayName("Returns nulls when update is missing message text")
    void returnsNullsWhenUpdateMissingMessageText() {
        Update update = new Update();

        assertThat(CommandUtils.hasText(update)).isFalse();
        assertThat(CommandUtils.getText(update)).isNull();
        assertThat(CommandUtils.getCommand(update)).isNull();
        assertThat(CommandUtils.getParts(update)).isEmpty();
        assertThat(CommandUtils.getChatId(update)).isNull();
    }

    @Test
    @DisplayName("Extracts command and parts from update text")
    void extractsCommandAndPartsFromUpdateText() {
        Update update = buildUpdate("/deposit 7 12.50", 17L);

        assertThat(CommandUtils.hasText(update)).isTrue();
        assertThat(CommandUtils.getText(update)).isEqualTo("/deposit 7 12.50");
        assertThat(CommandUtils.getCommand(update)).isEqualTo("/deposit");
        assertThat(CommandUtils.getParts(update)).containsExactly("/deposit", "7", "12.50");
        assertThat(CommandUtils.getChatId(update)).isEqualTo(17L);
    }

    @Test
    @DisplayName("Matches commands case-insensitively")
    void matchesCommandsCaseInsensitively() {
        Update update = buildUpdate("/HeLp", 5L);

        assertThat(CommandUtils.isCommand(update, "/help")).isTrue();
        assertThat(CommandUtils.isCommand(update, "/users")).isFalse();
    }

    private Update buildUpdate(String text, Long chatId) {
        Message message = new Message();
        if (chatId != null) {
            message.setChat(new Chat(chatId, "private"));
        }
        message.setText(text);

        Update update = new Update();
        update.setMessage(message);
        return update;
    }
}



