package com.example.AI_DATA.bulletin.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.FileSystemResource;
import com.example.AI_DATA.bulletin.repository.BulletinRepository;
import com.example.AI_DATA.bulletin.model.Bulletin;
import com.fasterxml.jackson.core.type.TypeReference;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;


import java.io.File;
import java.util.*;

@Service
public class BulletinService {
    private final BulletinRepository bulletinRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public BulletinService(BulletinRepository bulletinRepository) {
        this.bulletinRepository = bulletinRepository;
    }

    public void save(Bulletin bulletin) {
        this.bulletinRepository.save(bulletin);
    }

    public void merge(Bulletin bulletin) { this.bulletinRepository.merge(bulletin); }

    public void deleteById(Long id) {this.bulletinRepository.deleteById(id);}

    public long getRowNumber() { return this.bulletinRepository.countRows(); }

    public long getLatestBulletinId() { return this.bulletinRepository.getLatestBulletinId(); }

    public void sendRequestToAIServer(String imagePath) {
        String url = "http://localhost:8081//AICOSS/image/prediction";
        File jpgFile = new File(imagePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        FileSystemResource fileSystemResource = new FileSystemResource(jpgFile);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("jpgFile", fileSystemResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            ObjectMapper objectMapper = new ObjectMapper();

            Map<String, String> resultMap = objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
        }
    }



    public Optional<Bulletin> findById(Long id) {

        Optional<Bulletin> bulletin =  bulletinRepository.findById(id);

        return bulletin;
    }

}
