package com.example.AI_DATA;

import com.example.AI_DATA.user.service.UserService;
import com.example.AI_DATA.user.model.User;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.*;

public class UserApiController {
    private final UserService userService;

    @Autowired
    public UserApiController(UserService userService) {this.userService = userService;}

    @PostMapping("/user/save")
    public ResponseEntity<String> save(@RequestBody User user) {
        String userId = user.getId();
        String userPassword = user.getPassword();

        if (!userService.isValidId(userId)) {return userResponse("Invalid Id format", HttpStatus.BAD_REQUEST); }

        if (!userService.isValidPassword(userPassword)) {return userResponse("Invalid Password Format", HttpStatus.BAD_REQUEST);}

        userService.save(user);

        return userResponse("User save Successful", HttpStatus.OK);
    }

    @PostMapping("/user/login")
    public ResponseEntity<String> login(HttpServletRequest request) {
        String loginId = request.getParameter("loginId");
        String loginPassword = request.getParameter("loginPassword");

        if (!this.userService.login(loginId, loginPassword)) { return userResponse("Login Failed", HttpStatus.BAD_REQUEST); }

        else {
            HttpSession httpSession = request.getSession();
            httpSession.setAttribute("loginId", loginId);
            httpSession.setMaxInactiveInterval(60 * 30);
            return userResponse("Login Success", HttpStatus.OK);
        }

    }


    private ResponseEntity<String> userResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(message, status);
    }
}
