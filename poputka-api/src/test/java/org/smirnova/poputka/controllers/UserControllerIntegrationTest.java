package org.smirnova.poputka.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.smirnova.poputka.auth.domain.dto.AuthDTO;
import org.smirnova.poputka.domain.dto.UserEditDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void contextLoads() {
        Assertions.assertNotNull(mockMvc, "MockMvc should be initialized");
    }

    @Test
    void testUnauthorizedAccess() throws Exception {
        // Проверяем доступ к защищенному роуту без токена
        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isUnauthorized());

        // Проверяем доступ к конкретному пользователю без токена
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isUnauthorized());

        // Проверяем обновление пользователя без токена
        UserEditDto userEdit = UserEditDto.builder()
                .firstName("UpdatedName")
                .lastName("UpdatedLastName")
                .build();

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userEdit)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testLoginUser() throws Exception {
        AuthDTO.LoginRequest loginRequest = new AuthDTO.LoginRequest("testuser@example.com", "password");

        MvcResult result = mockMvc.perform(post("/api/util/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        AuthDTO.Response authResponse = objectMapper.readValue(responseContent, AuthDTO.Response.class);

        Assertions.assertNotNull(authResponse.token(), "Token should not be null");
    }

    @Test
    void testGetAllUsers() throws Exception {
        String token = authenticateAndGetToken();

        mockMvc.perform(get("/api/users/all")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testGetUserById() throws Exception {
        String token = authenticateAndGetToken();

        mockMvc.perform(get("/api/users/1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    private String authenticateAndGetToken() throws Exception {
        AuthDTO.LoginRequest loginRequest = new AuthDTO.LoginRequest("testuser@example.com", "password");

        MvcResult result = mockMvc.perform(post("/api/util/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        AuthDTO.Response authResponse = objectMapper.readValue(responseContent, AuthDTO.Response.class);
        return authResponse.token();
    }
}
