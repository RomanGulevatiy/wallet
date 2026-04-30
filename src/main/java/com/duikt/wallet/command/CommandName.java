package com.duikt.wallet.command;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommandName {

    START("/start"),
    HELP("/help"),
    USERS("/users"),
    PROFILE("/profile"),
    DEPOSIT("/deposit"),
    WITHDRAW("/withdraw"),
    UNKNOWN("unknown");

    private final String name;
}
