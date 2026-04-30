package com.duikt.wallet.command;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandHandlerTest {

    @Mock
    private Command firstCommand;

    @Mock
    private Command secondCommand;

    @Test
    @DisplayName("Delegates to the first command that can handle the update")
    void delegatesToFirstMatchingCommand() {
        Update update = new Update();

        when(firstCommand.canHandle(update)).thenReturn(false);
        when(secondCommand.canHandle(update)).thenReturn(true);

        CommandHandler handler = new CommandHandler(List.of(firstCommand, secondCommand));
        handler.handle(update);

        verify(secondCommand).handle(update);
        verify(firstCommand, never()).handle(update);
    }

    @Test
    @DisplayName("Does nothing when no command matches the update")
    void doesNothingWhenNoCommandMatches() {
        Update update = new Update();

        when(firstCommand.canHandle(update)).thenReturn(false);
        when(secondCommand.canHandle(update)).thenReturn(false);

        CommandHandler handler = new CommandHandler(List.of(firstCommand, secondCommand));
        handler.handle(update);

        verify(firstCommand, never()).handle(update);
        verify(secondCommand, never()).handle(update);
    }
}

