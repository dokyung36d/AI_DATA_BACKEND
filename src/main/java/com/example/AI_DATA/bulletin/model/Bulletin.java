package com.example.AI_DATA.bulletin.model;


import lombok.Data;
import jakarta.persistence.Lob;

@Data
public class Bulletin {
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
