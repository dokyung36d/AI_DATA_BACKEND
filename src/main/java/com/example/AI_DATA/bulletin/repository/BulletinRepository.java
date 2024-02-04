package com.example.AI_DATA.bulletin.repository;

import com.example.AI_DATA.bulletin.model.Bulletin;

import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.transaction.Transactional;

import java.util.*;

@Repository
public class BulletinRepository {

    private final EntityManager entityManager;
    @Autowired
    public BulletinRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    public void save(Bulletin bulletin) {
        entityManager.persist(bulletin);
    }

    @Transactional
    public void deleteById(Long bulletinId) {
        entityManager.remove(findById(bulletinId).get());
    }

    @Transactional
    public Optional<Bulletin> findById(Long bulletinId) {
        Bulletin bulletin =  entityManager.find(Bulletin.class, bulletinId);
        System.out.println(bulletin);
        return Optional.ofNullable(bulletin);
    }

    @Transactional
    public void merge(Bulletin bulletin) { entityManager.merge(bulletin); }

    public Optional<Bulletin> findByTitle(String findTitle) {
        Bulletin bulletin =  entityManager.createQuery("SELECT b From Bulletin b WHERE b.title = :findTitle", Bulletin.class)
                .setParameter("findTitle", findTitle)
                .getSingleResult();
        return Optional.ofNullable(bulletin);
    }

    public List<Bulletin> findAll() {
        return entityManager.createQuery("SELECT b FROM Bulletin b", Bulletin.class)
                .getResultList();
    }
}
