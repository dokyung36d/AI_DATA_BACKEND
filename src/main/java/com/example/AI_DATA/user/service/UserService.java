package com.example.AI_DATA.user.service;

import com.example.AI_DATA.user.model.User;
import com.example.AI_DATA.user.repository.UserRepository;

import jakarta.transaction.Transactional;
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

    @Transactional
    public void save(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());

        user.setPassword(encodedPassword);
        this.userRepository.save(user);
    }

    @Transactional
    public void deleteById(String id) {this.userRepository.deleteById(id);}

    @Transactional
    public Optional<User> findById(String id) {
        Optional<User> user = this.userRepository.findById(id);

        return user;
    }

    @Transactional
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

    public boolean checkIdPasswordMatching(String userId, String userPassword) {
        Optional<User> user = this.userRepository.findById(userId);

        if (user.isEmpty()) { return false; }

        String encodedUserPassword = passwordEncoder.encode(userPassword);
        if (user.get().getPassword() == encodedUserPassword) { return true; }


        return false;

    }



}
