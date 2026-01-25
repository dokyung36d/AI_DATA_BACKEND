package com.example.AI_DATA.bulletin.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.FileSystemResource;
import com.example.AI_DATA.bulletin.repository.BulletinRepository;
import com.example.AI_DATA.bulletin.model.Bulletin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

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
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger; // 1. import 추가
import org.slf4j.LoggerFactory;

@Service
public class BulletinService {
    private final BulletinRepository bulletinRepository;
    private static final Logger log = LoggerFactory.getLogger(BulletinService.class);

    @Value("${ai.server.url}")
    private String aiServerUrl;

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

    @Async
    public CompletableFuture<Void> sendRequestToAIServer(long id, String imagePath) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);


        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("imagePath", imagePath); // 이제 파일이 아니라 URL 문자열임

        // 3. 요청 엔티티 생성
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.exchange(aiServerUrl, HttpMethod.POST, requestEntity, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                // 결과는 안 받더라도, 요청 자체가 실패했다면 로그는 남겨야 함
                log.error("AI 서버 요청 실패 - 상태 코드: {}, ID: {}", response.getStatusCode(), id);
            }
        } catch (Exception e) {
            // 네트워크 장애, 타임아웃 등 예외 발생 시 로그 기록
            log.error("AI 서버 통신 중 예외 발생 - ID: {}, Error: {}", id, e.getMessage());

            // 호출부에 예외를 전달하고 싶다면 exceptionally 처리
            return CompletableFuture.failedFuture(e);
        }

        return CompletableFuture.completedFuture(null);
    }

    public Optional<Bulletin> findById(Long id) {

        Optional<Bulletin> bulletin =  bulletinRepository.findById(id);

        return bulletin;
    }

    public String preprocessRestResponseToString(ResponseEntity<String> response) {
        String jsonString = response.getBody().substring(1, response.getBody().length() - 1); // to remove /" first and last
        jsonString = jsonString.replace("\\\"", "\"");

        return jsonString;
    }

}
