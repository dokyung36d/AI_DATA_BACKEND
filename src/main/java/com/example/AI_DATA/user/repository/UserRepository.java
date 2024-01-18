package com.example.AI_DATA.user.repository;

import com.example.AI_DATA.user.model.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.swing.text.html.Option;
import java.util.*;

@Repository
public class UserRepository {
    private final EntityManager entityManager;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserRepository(EntityManager entityManager, PasswordEncoder passwordEncoder) {
        this.entityManager = entityManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void save(User user) {
        entityManager.persist(user);
    }

    @Transactional
    public void deleteById(String id) {
        entityManager.remove(findById(id).get());
    }

    @Transactional
    public Optional<User> findById(String id) {
        User user = entityManager.find(User.class, id);

        return Optional.ofNullable(user);
    }

    @Transactional
    public boolean checkTruePassword(String userId, String checkPassword) {
        Optional<User> user = findById(userId);

        if (user.isEmpty()) {
            return false;
        }
        String encodedCheckPassword = passwordEncoder.encode(checkPassword);

        String encodedOldPassword = user.get().getPassword();

        if (encodedCheckPassword.equals(encodedOldPassword))  { return true; }

        else { return false; }
    }

    @Transactional
    public void updatePassword(String id, String newPassword) {
        User user = entityManager.find(User.class, id);
        user.setPassword(passwordEncoder.encode(newPassword));
        entityManager.merge(user);
    }
}
