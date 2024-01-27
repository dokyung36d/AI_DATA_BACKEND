package com.example.AI_DATA;

import com.example.AI_DATA.user.model.User;
import com.example.AI_DATA.user.service.UserService;
import com.example.AI_DATA.UserApiController;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.BufferedReader;
import java.lang.reflect.Executable;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String testId = "testId";
    private String testPassword = "testPassword";

//    @BeforeEach
//    void before()

    @Test
    @DisplayName("Password Encoder test")
    @Order(1)
    public void passwordEncoderTest() {
        String rawPassword = "qwerty";

        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertAll(
                () -> assertNotEquals(rawPassword, encodedPassword),
                () -> assertTrue(passwordEncoder.matches(rawPassword, encodedPassword))
        );
    }

    @Test
    @Order(2)
    @DisplayName("Create account Test")
    public void createAccountTest() throws Exception {
//        UserApiController userApiController = new UserApiController(userService);
//
//        mockMvc = MockMvcBuilders.standaloneSetup(userApiController).build();

        User user = new User(testId, testPassword);
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

//        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
//                .post("/user/save")
//                .contentType("application/json")
//                .content(userJson);


        mockMvc.perform(post("/user/save")
                        .contentType("application/json")
                        .content(userJson))
                        .andExpect(status().isOk())
                        .andExpect(content().string("User save Successful"));
    }

    @Test
    @Order(3)
    @DisplayName("Delete account Test")
    void deleteAccountTest() throws Exception {
        String requestBody = "{ \"id\":\"testId\", \"password\":\"testPassword\" }";

        mockMvc.perform(delete("/user/delete")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().string("Delete Account Successful"));

    }


}
