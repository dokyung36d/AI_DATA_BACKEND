package com.example.AI_DATA.user.repository;

import com.example.AI_DATA.user.model.User;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.persistence.Query;

import javax.swing.text.html.Option;
import java.util.*;

@Repository
public class UserRepository {
    private final EntityManager entityManager;

    @Autowired
    public UserRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    public void save(User user) {
        entityManager.persist(user);
    }

    public void update(User user) {entityManager.merge(user); }


    public void deleteById(String id) {
        entityManager.remove(findById(id).get());
    }


    public Optional<User> findById(String id) {
        User user = entityManager.find(User.class, id);

        return Optional.ofNullable(user);
    }


    public List<String> getAllUserIds() {
        // Use JPQL to select only the 'id' attribute
        String jpql = "SELECT u.id FROM com.example.AI_DATA.user.model.User u";
        Query query = entityManager.createQuery(jpql);

        // Execute the query and retrieve the results
        List<String> userIds = query.getResultList();

        return userIds;
    }

    public boolean checkTruePassword(String userId, String checkPassword) {
        Optional<User> user = findById(userId);

        if (user.isEmpty()) {
            return false;
        }

        if (checkPassword.equals(user.get().getPassword()))  { return true; }

        else { return false; }
    }


    public void updatePassword(String id, String newPassword) {
        User user = entityManager.find(User.class, id);
        user.setPassword(newPassword);
        entityManager.merge(user);
    }
}
