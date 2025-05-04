package com.tericcabrel.authapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tericcabrel.authapi.dtos.LoginUserDto;
import com.tericcabrel.authapi.dtos.RegisterUserDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void signupShouldReturnJwtToken() throws Exception {
        RegisterUserDto dto = new RegisterUserDto();
        dto.setEmail("test1@example.com");
        dto.setPassword("password");
        dto.setFullName("Test User 1");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void loginShouldFailWithWrongCredentials() throws Exception {
        LoginUserDto dto = new LoginUserDto();
        dto.setEmail("fake@example.com");
        dto.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}
