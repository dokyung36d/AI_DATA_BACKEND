package com.example.AI_DATA.bulletin.repository;

import com.example.AI_DATA.bulletin.model.Bulletin;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class BulletinRepository {
    private static Map<Long, Bulletin> store = new HashMap<>();
    private static Long sequence = 0l;

    public void save(Bulletin bulletin) {
        bulletin.setId(++sequence);
        store.put(bulletin.getId(), bulletin);
    }

    public Optional<Bulletin> findById(Long bulletinId) {
        return Optional.ofNullable(store.get(bulletinId));
    }

}
