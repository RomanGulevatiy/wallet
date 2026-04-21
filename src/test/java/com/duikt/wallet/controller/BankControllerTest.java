package com.duikt.wallet.controller;

import com.duikt.wallet.dto.UserDto;
import com.duikt.wallet.service.BankService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BankController.class)
class BankControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankService bankService;

    @Test
    @DisplayName("Returns created when create user request uses the API prefix")
    void createUserReturnsCreatedWhenRequestUsesApiPrefix() throws Exception {
        mockMvc.perform(post("/api/user/create").param("name", "MyName"))
                .andExpect(status().isCreated());

        verify(bankService).createUser("MyName");
    }

    @Test
    @DisplayName("Returns not found when create user request misses the API prefix")
    void createUserReturnsNotFoundWhenRequestMissesApiPrefix() throws Exception {
        mockMvc.perform(post("/user/create").param("name", "MyName"))
                .andExpect(status().isNotFound());

        verifyNoInteractions(bankService);
    }

    @Test
    @DisplayName("Returns bad request when create user name parameter is missing")
    void createUserReturnsBadRequestWhenNameParameterIsMissing() throws Exception {
        mockMvc.perform(post("/api/user/create"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bankService);
    }

    @Test
    @DisplayName("Returns a user DTO for an existing user")
    void getUserByIdReturnsUserDtoForExistingUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Alice")
                .balance(new BigDecimal("25.00"))
                .build();

        when(bankService.getUserDtoById(1L)).thenReturn(userDto);

        mockMvc.perform(get("/api/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Alice"))
                .andExpect(jsonPath("$.balance").value(25.00));
    }

    @Test
    @DisplayName("Returns a list of user DTOs")
    void getAllUsersReturnsListOfUserDto() throws Exception {
        UserDto first = UserDto.builder()
                .id(1L)
                .name("Alice")
                .balance(new BigDecimal("10.00"))
                .build();
        UserDto second = UserDto.builder()
                .id(2L)
                .name("Bob")
                .balance(new BigDecimal("20.00"))
                .build();

        when(bankService.getAllUsers()).thenReturn(List.of(first, second));

        mockMvc.perform(get("/api/users").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[0].balance").value(10.00))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Bob"))
                .andExpect(jsonPath("$[1].balance").value(20.00));
    }

    @Test
    @DisplayName("Returns ok and delegates withdraw to the service")
    void withdrawReturnsOkAndDelegatesToService() throws Exception {
        mockMvc.perform(post("/api/user/7/withdraw").param("amount", "12.50"))
                .andExpect(status().isOk());

        verify(bankService).withdraw(7L, new BigDecimal("12.50"));
    }

    @Test
    @DisplayName("Returns bad request when withdraw amount parameter is missing")
    void withdrawReturnsBadRequestWhenAmountParameterIsMissing() throws Exception {
        mockMvc.perform(post("/api/user/7/withdraw"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bankService);
    }

    @Test
    @DisplayName("Returns ok and delegates deposit to the service")
    void depositReturnsOkAndDelegatesToService() throws Exception {
        mockMvc.perform(post("/api/user/7/deposit").param("amount", "12.50"))
                .andExpect(status().isOk());

        verify(bankService).deposit(7L, new BigDecimal("12.50"));
    }

    @Test
    @DisplayName("Returns bad request when deposit amount parameter is missing")
    void depositReturnsBadRequestWhenAmountParameterIsMissing() throws Exception {
        mockMvc.perform(post("/api/user/7/deposit"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bankService);
    }
}


