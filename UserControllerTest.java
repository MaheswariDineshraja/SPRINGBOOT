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
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Helper method to register and get JWT token
    private String getJwtToken(String email) throws Exception {
        RegisterUserDto registerUser = new RegisterUserDto();
        registerUser.setEmail(email);
        registerUser.setPassword("password123");
        registerUser.setFullName("JWT Test User");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerUser)))
                .andExpect(status().isOk());

        LoginUserDto loginUser = new LoginUserDto();
        loginUser.setEmail(email);
        loginUser.setPassword("password123");

        MvcResult result = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginUser)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("token").asText();
    }

    @Test
    void userProfileShouldReturnUserDetailsWithValidJwt() throws Exception {
        String token = getJwtToken("usercontroller@example.com");

        mockMvc.perform(get("/users/me") // or "/userprofile" if you use that path
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("usercontroller@example.com")));
    }

    @Test
    void userProfileShouldReturnUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
