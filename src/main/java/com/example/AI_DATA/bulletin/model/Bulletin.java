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

    @Lob
    private byte[] imageData;

    public Bulletin(String title, String label, byte[] imageData) {
        this.title = title;
        this.label = label;
        this.imageData = imageData;
    }

    public Bulletin() {}
}
