package com.example.AI_DATA.bulletin.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "bulletin")
public class Bulletin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name=  "id")
    private Long id;

    @Column(nullable = false)
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
