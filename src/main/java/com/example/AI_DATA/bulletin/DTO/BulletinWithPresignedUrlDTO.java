package com.example.AI_DATA.bulletin.DTO;

import com.example.AI_DATA.bulletin.model.Bulletin;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;


@Getter
@Setter
@AllArgsConstructor
public class BulletinWithPresignedUrlDTO {
    private Bulletin bulletin;
    private String presignedUrl;

    public BulletinWithPresignedUrlDTO(Bulletin bulletin, String presignedUrl) {
        this.bulletin = bulletin;
        this.presignedUrl = presignedUrl;
    }

    public Bulletin getBulletin() {
        return bulletin;
    }

    public String getPresignedUrl() {
        return presignedUrl;
    }

    public void setBulletin(Bulletin bulletin) {
        this.bulletin = bulletin;
    }

    public void setPresignedUrl(String presignedUrl) {
        this.presignedUrl = presignedUrl;
    }
}