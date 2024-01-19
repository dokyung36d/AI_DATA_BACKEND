package com.example.AI_DATA.user.service;

import com.example.AI_DATA.user.model.User;
import com.example.AI_DATA.user.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;}

    public void save(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);
        this.userRepository.save(user);
    }

    public void deleteById(String id) {this.userRepository.deleteById(id);}

    public Optional<User> findById(String id) {
        Optional<User> user = this.userRepository.findById(id);

        return user;
    }

    public boolean login(String loginId, String loginPassword) {
        Optional<User> user = this.userRepository.findById(loginId);

        if (user.isEmpty()) { return false; }

        if (user.get().getPassword() != passwordEncoder.encode(loginPassword)) { return false; }

        return true;

    }

    public boolean isValidId(String userId) {
        if (userId.length() >= 6) { return false; }

        List<String> idList = userRepository.getAllUserIds();
        if (idList.contains(userId)) { return false; }

        return true;
    }

    public boolean isValidPassword(String userPassword) {
        return userPassword.length() >= 6;
    }



}
