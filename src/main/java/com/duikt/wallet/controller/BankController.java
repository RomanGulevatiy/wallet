package com.duikt.wallet.controller;

import com.duikt.wallet.dto.UserDto;
import com.duikt.wallet.service.BankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;

    @PostMapping("/user/create")
    @ResponseStatus(HttpStatus.CREATED)
    public void createUser(@RequestParam String name) {
        bankService.createUser(name);
    }

    @GetMapping("/user/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        return bankService.getUserDtoById(userId);
    }

    @GetMapping("/users")
    public List<UserDto> getAllUsers() {
        return bankService.getAllUsers();
    }

    @PostMapping("/user/{userId}/withdraw")
    @ResponseStatus(HttpStatus.OK)
    public void withdraw(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount) {
        bankService.withdraw(userId, amount);
    }

    @PostMapping("/user/{userId}/deposit")
    @ResponseStatus(HttpStatus.OK)
    public void deposit(
            @PathVariable Long userId,
            @RequestParam BigDecimal amount) {
        bankService.deposit(userId, amount);
    }
}
