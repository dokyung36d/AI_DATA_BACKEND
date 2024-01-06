package com.example.AI_DATA.bulletin.Service;

import com.example.AI_DATA.bulletin.repository.BulletinRepository;
import com.example.AI_DATA.bulletin.model.Bulletin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BulletinService {
    private final BulletinRepository bulletinRepository;

    @Autowired
    public BulletinService(BulletinRepository bulletinRepository) {
        this.bulletinRepository = bulletinRepository;
    }

    public void save(Bulletin bulletin) {
        this.bulletinRepository.save(bulletin);
    }

    public void deleteById(Long id) {this.bulletinRepository.deleteById(id);}

    public Optional<Bulletin> findById(Long id) {
        return this.bulletinRepository.findById(id);
    }

}
