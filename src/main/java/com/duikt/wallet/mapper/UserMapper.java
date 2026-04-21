package com.duikt.wallet.mapper;

import com.duikt.wallet.dto.UserDto;
import com.duikt.wallet.entity.User;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        if(user == null) return null;

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .balance(user.getWallet() != null ? (user.getWallet().getBalance()) : BigDecimal.ZERO)
                .build();
    }
}
