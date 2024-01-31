package com.example.AI_DATA;

import com.example.AI_DATA.dto.user.PasswordChangeDTO;
import com.example.AI_DATA.user.model.User;
import com.example.AI_DATA.user.service.UserService;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.junit.jupiter.api.Assertions.*;
import jakarta.servlet.http.HttpSession;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MockMvc mockMvc1;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String testId = "testId";
    private String testPassword = "testPassword";
    private String changedPassword = "changedNewPassword";

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
    @DisplayName("Login and Logout Test")
    void loginTest() throws Exception {
        //Login Test
        User user = new User(testId, testPassword);
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        MvcResult mvcResult = mockMvc.perform(post("/user/login")
                    .contentType("application/json")
                    .content(userJson))
                    .andExpect(status().isOk())
                    .andDo(MockMvcResultHandlers.print())
                    .andReturn();

        Object suggestedId = mvcResult.getRequest().getSession().getAttribute("loginId");

        assertNotNull(suggestedId);
        assertEquals(testId, suggestedId.toString());

        MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession();

        //Logout Test

        MvcResult mvcResult1 = mockMvc.perform(post("/user/logout")
                .session(session))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @Order(4)
    @DisplayName("Double Login Test")
    void checkDoubleLogin() throws Exception {
        //Login Test
        User user = new User(testId, testPassword);
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        MvcResult mvcResult = mockMvc.perform(post("/user/login")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        //Double login

        MvcResult mvcResult1 = mockMvc1.perform(post("/user/login")
                        .contentType("application/json")
                        .content(userJson))
                .andExpect(status().isOk())
                .andExpect(content().string("you already login, So Previous Session Removed"))
                .andReturn();
    }

    @Test
    @Order(5)
    @DisplayName("Password Change Test")
    void changeAccountTest() throws Exception {
        MockHttpSession session = loginAndReturnSession();

        PasswordChangeDTO passwordChangeDTO = new PasswordChangeDTO(testPassword, changedPassword);
        ObjectMapper objectMapper = new ObjectMapper();
        String passwordChangeDTOJson = objectMapper.writeValueAsString(passwordChangeDTO);

        assertNotNull(session.getAttribute("loginId"));

        mockMvc.perform(post("/user/changePassword")
                        .session(session)
                .contentType("application/json")
                .content(passwordChangeDTOJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Password Change Successful"));
    }


    @Test
    @Order(6)
    @DisplayName("Delete account Test")
    void deleteAccountTest() throws Exception {
//        String requestBody = "{ \"id\":\"testId\", \"password\":\"testPassword\" }";

        User user = new User(testId, changedPassword);
        ObjectMapper objectMapper = new ObjectMapper();
        String deleteJson = objectMapper.writeValueAsString(user);


        mockMvc.perform(delete("/user/delete")
                .contentType("application/json")
                .content(deleteJson))
                .andExpect(status().isOk())
                .andExpect(content().string("Delete Account Successful"));

    }

    MockHttpSession loginAndReturnSession() throws Exception {
        User user = new User(testId, testPassword);
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        MvcResult mvcResult = mockMvc.perform(post("/user/login")
                .contentType("application/json")
                .content(userJson))
                .andReturn();


        MockHttpSession session = (MockHttpSession) mvcResult.getRequest().getSession();

        assertNotNull(session.getAttribute("loginId"));

        return session;
    }

}
