package com.example.AI_DATA.bulletin.model;


import lombok.Data;
import jakarta.persistence.Lob;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Data
@Entity
public class Bulletin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;
    private String label;

    @Lob
    private byte[] imageData;

    public Bulletin(String title, String label, byte[] imageData) {
        this.title = title;
        this.label = label;
        this.imageData = imageData;
    }
}
