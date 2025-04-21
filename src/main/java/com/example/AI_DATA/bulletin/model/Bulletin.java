package com.example.AI_DATA.bulletin.model;


import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "bulletin")
public class Bulletin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name=  "id")
    private Long id;

    private String title;
    private String label;
    private String imageFilePath;


    public Bulletin(String title, String label, String imageFilePath) {
        this.title = title;
        this.label = label;
        this.imageFilePath = imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = imageFilePath;
    }

    public String getImageFilePath() {
        return this.imageFilePath;
    }
}
