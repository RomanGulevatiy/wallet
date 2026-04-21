package com.duikt.wallet.service;

import com.duikt.wallet.dto.UserDto;
import com.duikt.wallet.entity.User;
import com.duikt.wallet.entity.Wallet;
import com.duikt.wallet.exception.UserNotFoundException;
import com.duikt.wallet.exception.WalletException;
import com.duikt.wallet.mapper.UserMapper;
import com.duikt.wallet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankService {

    private final UserRepository userRepository;
    private final UserMapper mapper;

    /** USER */
    @Transactional
    public void createUser(String name) {
        log.info("Creating user with name={}", name);

        User user = new User();
        user.setName(name);

        Wallet wallet = new Wallet();
        wallet.setBalance(BigDecimal.ZERO);
        user.setWallet(wallet);

        userRepository.save(user);
        log.info("Created user id={} with initial wallet balance={}", user.getId(), user.getWallet().getBalance());
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        log.debug("Fetching all users");

        List<UserDto> users = userRepository.findAll().stream()
                .map(mapper::toUserDto)
                .toList();

        log.debug("Fetched {} users", users.size());
        return users;
    }

    @Transactional(readOnly = true)
    public UserDto getUserDtoById(Long userId) {
        log.info("Fetching user by id={}", userId);

        UserDto userDto = mapper.toUserDto(userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id={} was not found", userId);
            return new UserNotFoundException("User with id " + userId + " not found");
        }));

        log.debug("Fetched user dto for id={} with balance={}", userId, userDto.getBalance());
        return userDto;
    }

    /** WALLET */
    @Transactional
    public void withdraw(Long userId, BigDecimal amount) {
        log.info("Withdrawing amount={} from user id={}", amount, userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id={} was not found for withdraw", userId);
            return new UserNotFoundException("User with id " + userId + " not found");
        });

        Wallet wallet = user.getWallet();
        if(wallet.getBalance().compareTo(amount) < 0) {
            log.warn("Insufficient balance for user id={}: balance={}, requested={}", userId, wallet.getBalance(), amount);
            throw new WalletException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        log.info("Withdraw completed for user id={}. New balance={}", userId, wallet.getBalance());
    }

    @Transactional
    public void deposit(Long userId, BigDecimal amount) {
        if(amount.compareTo(BigDecimal.ZERO) <= 0) {
            log.warn("Invalid deposit amount={} for user id={}", amount, userId);
            throw new WalletException("Amount must be greater than zero");
        }

        log.info("Depositing amount={} to user id={}", amount, userId);

        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("User with id={} was not found for deposit", userId);
            return new UserNotFoundException("User with id " + userId + " not found");
        });
        user.getWallet().setBalance(user.getWallet().getBalance().add(amount));
        log.info("Deposit completed for user id={}. New balance={}", userId, user.getWallet().getBalance());
    }
}
