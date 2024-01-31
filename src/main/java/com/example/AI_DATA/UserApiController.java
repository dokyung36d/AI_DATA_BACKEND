package com.example.AI_DATA;

import com.example.AI_DATA.user.config.SessionConfig;
import com.example.AI_DATA.user.service.UserService;
import com.example.AI_DATA.user.model.User;
import com.example.AI_DATA.user.config.SessionConfig.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;


@RestController
public class UserApiController {
    private final UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/user/save")
    public ResponseEntity<String> saveUser(@RequestBody User user) {
        String userId = user.getId();
        String userPassword = user.getPassword();

        if (!userService.isValidId(userId)) {
            return userResponse("Invalid Id format", HttpStatus.BAD_REQUEST);
        }

        if (!userService.isValidPassword(userPassword)) {
            return userResponse("Invalid Password Format", HttpStatus.BAD_REQUEST);
        }

        userService.save(user);

        return userResponse("User save Successful", HttpStatus.OK);
    }

    @PostMapping("/user/login")
    public ResponseEntity<String> login(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        map = convertHttpServletRequestToMap(request);

        String loginId = map.get("id");
        String loginPassword = map.get("password");
        HttpSession httpSession = request.getSession();

//        if (httpSession.getAttribute("loginId") != null) {
//            return userResponse("you already logined", HttpStatus.BAD_REQUEST);
//        }

        if (!this.userService.login(loginId, loginPassword)) {
            return userResponse("Login Failed", HttpStatus.BAD_REQUEST);
        }

        if (SessionConfig.checkSessionAlreadyExist(loginId)) {
            httpSession.setAttribute("loginId", loginId);
            httpSession.setMaxInactiveInterval(60 * 30);

            return userResponse("you already login, So Previous Session Removed", HttpStatus.OK);
        }

        httpSession.setAttribute("loginId", loginId);
        httpSession.setMaxInactiveInterval(60 * 30);
        return userResponse("Login Success", HttpStatus.OK);

    }

    @PostMapping("/user/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        HttpSession httpSession = request.getSession(false);

        if (httpSession == null || httpSession.getAttribute("loginId") == null) {
            return userResponse("You first login first", HttpStatus.UNAUTHORIZED);
        }

        httpSession.invalidate();
        return userResponse("Logout Successful", HttpStatus.OK);
    }

    @PostMapping("/user/changePassword")
    public ResponseEntity<String> changePassword(HttpServletRequest request) {
        HttpSession httpSession = request.getSession(false);

        if (httpSession == null || httpSession.getAttribute("loginId") == null) {
            return userResponse("you first login first to change password", HttpStatus.UNAUTHORIZED);
        }

        Map<String, String> map = new HashMap<>();
        map = convertHttpServletRequestToMap(request);

        String newPassword = map.get("newPassword");

        if (!userService.isValidPassword(newPassword)) {
            return userResponse("Invalid Password Format", HttpStatus.BAD_REQUEST);
        }

        Optional<User> user = userService.findById(httpSession.getAttribute("loginId").toString());

        if (user.isEmpty()) {
            return userResponse("User Not Found", HttpStatus.NOT_FOUND);
        }

        user.get().setPassword(newPassword);
        userService.update(user.get());

        return userResponse("Password Change Successful", HttpStatus.OK);
    }

    @DeleteMapping("/user/delete")
    public ResponseEntity<String> deleteUser(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        map = convertHttpServletRequestToMap(request);

        String loginId = map.get("id");
        String loginPassword = map.get("password");



        HttpSession httpSession = request.getSession();

        if (httpSession.getAttribute("loginId") != null) {
            return userResponse("you first login to delete your account", HttpStatus.BAD_REQUEST);
        }

        if (userService.findById(loginId).isEmpty()) {
            return userResponse("Not Found User", HttpStatus.NOT_FOUND);
        }

        if (!userService.checkIdPasswordMatching(loginId, loginPassword)) {
            String suggestedPassword = passwordEncoder.encode(loginPassword);
            String savedPassword = userService.findById(loginId).get().getPassword();
            return userResponse("Wrong Password\n suggested password: " + suggestedPassword +
                    "savedPassword : " + savedPassword, HttpStatus.UNAUTHORIZED);
        }

        userService.deleteById(loginId);

        return userResponse("Delete Account Successful", HttpStatus.OK);

    }

    private ResponseEntity<String> userResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(message, status);
    }

    public Map<String, String> convertHttpServletRequestToMap(HttpServletRequest httpServletRequest) {
        Map<String, String> map = new HashMap<>();

        try (BufferedReader reader = httpServletRequest.getReader()) {
            StringBuilder requestBody = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }


            ObjectMapper objectMapper = new ObjectMapper();

            map = objectMapper.readValue(requestBody.toString(), HashMap.class);


        } catch (IOException e) {
            // Handle IOException appropriately
            e.printStackTrace();
        }

        return map;
    }
}