package com.duikt.wallet.service;

import com.duikt.wallet.dto.UserDto;
import com.duikt.wallet.entity.User;
import com.duikt.wallet.entity.Wallet;
import com.duikt.wallet.exception.UserNotFoundException;
import com.duikt.wallet.exception.WalletException;
import com.duikt.wallet.mapper.UserMapper;
import com.duikt.wallet.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;

    @InjectMocks
    private BankService bankService;

    @Test
    @DisplayName("Creates a user with a zero-balance wallet")
    void createsUserWithWalletInitializedToZero() {
        bankService.createUser("MyName");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getName()).isEqualTo("MyName");
        assertThat(savedUser.getWallet()).isNotNull();
        assertThat(savedUser.getWallet().getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Returns mapped users when getting all users")
    void returnsMappedUsersWhenGettingAllUsers() {
        User user = new User();
        user.setId(1L);
        user.setName("Alice");

        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("12.50"));
        user.setWallet(wallet);

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Alice")
                .balance(new BigDecimal("12.50"))
                .build();

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(mapper.toUserDto(user)).thenReturn(userDto);

        List<UserDto> result = bankService.getAllUsers();

        assertThat(result).containsExactly(userDto);
    }

    @Test
    @DisplayName("Returns a user DTO when the user exists")
    void returnsUserDtoWhenUserExists() {
        User user = new User();
        user.setId(7L);
        user.setName("Bob");

        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("99.99"));
        user.setWallet(wallet);

        UserDto userDto = UserDto.builder()
                .id(7L)
                .name("Bob")
                .balance(new BigDecimal("99.99"))
                .build();

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(mapper.toUserDto(user)).thenReturn(userDto);

        UserDto result = bankService.getUserDtoById(7L);

        assertThat(result).isEqualTo(userDto);
    }

    @Test
    @DisplayName("Throws UserNotFoundException when getting a missing user")
    void throwsUserNotFoundExceptionWhenGettingMissingUser() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankService.getUserDtoById(99L))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with id 99 not found");
    }

    @Test
    @DisplayName("Withdraws an amount from the wallet when balance is sufficient")
    void withdrawsAmountFromWalletWhenBalanceIsSufficient() {
        User user = new User();
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("100.00"));
        user.setWallet(wallet);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        bankService.withdraw(1L, new BigDecimal("25.00"));

        assertThat(user.getWallet().getBalance()).isEqualByComparingTo("75.00");
    }

    @Test
    @DisplayName("Throws WalletException when withdraw amount exceeds balance")
    void throwsWalletExceptionWhenWithdrawAmountExceedsBalance() {
        User user = new User();
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("10.00"));
        user.setWallet(wallet);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> bankService.withdraw(1L, new BigDecimal("10.01")))
                .isInstanceOf(WalletException.class)
                .hasMessage("Insufficient balance");
    }

    @Test
    @DisplayName("Deposits an amount into the wallet when amount is positive")
    void depositsAmountIntoWalletWhenAmountIsPositive() {
        User user = new User();
        Wallet wallet = new Wallet();
        wallet.setBalance(new BigDecimal("15.00"));
        user.setWallet(wallet);

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        bankService.deposit(2L, new BigDecimal("5.25"));

        assertThat(user.getWallet().getBalance()).isEqualByComparingTo("20.25");
    }

    @Test
    @DisplayName("Throws WalletException when deposit amount is not positive")
    void throwsWalletExceptionWhenDepositAmountIsNotPositive() {
        assertThatThrownBy(() -> bankService.deposit(2L, BigDecimal.ZERO))
                .isInstanceOf(WalletException.class)
                .hasMessage("Amount must be greater than zero");
    }

    @Test
    @DisplayName("Throws UserNotFoundException when depositing to a missing user")
    void throwsUserNotFoundExceptionWhenDepositingToMissingUser() {
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankService.deposit(3L, new BigDecimal("1.00")))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with id 3 not found");
    }
}

