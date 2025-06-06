package com.example.AI_DATA;

import com.example.AI_DATA.bulletin.DTO.BulletinWithPresignedUrlDTO;
import com.example.AI_DATA.bulletin.Service.BulletinService;
import com.example.AI_DATA.bulletin.Service.S3PresignedUrlService;
import com.example.AI_DATA.bulletin.Service.S3Uploader;
import com.example.AI_DATA.bulletin.model.Bulletin;
import com.example.AI_DATA.bulletin.Service.S3PresignedUrlService;
import com.example.AI_DATA.restapi.Message;
import com.example.AI_DATA.restapi.RestResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.Part;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Collectors;
import java.util.*;

@RestController
public class BulletinApiController {
    private final BulletinService bulletinService;
    Path basePath = Paths.get("C:\\Users\\dokyu\\OneDrive - UOS\\바탕 화면\\AI_DATA\\image");

    @Autowired
    public BulletinApiController(BulletinService bulletinService) {
        this.bulletinService = bulletinService;
    }

    @Autowired
    private S3Uploader s3Uploader;

    @Autowired
    private S3PresignedUrlService s3PresignedUrlService;

    @GetMapping("/bulletin/view/{id}")
    public ResponseEntity<RestResponse> findBulletin(@PathVariable("id") Long id) {
        RestResponse<Object> restResponse = new RestResponse<>();
        Optional<Bulletin> bulletin = bulletinService.findById(id);


        if (!bulletin.isEmpty()) {
            String key = bulletin.get().getImageFilePath();
            String presignedUrl = s3PresignedUrlService.generatePresignedUrl(key, 10);
            BulletinWithPresignedUrlDTO responseData = new BulletinWithPresignedUrlDTO(bulletin.get(), presignedUrl);
            restResponse = RestResponse.builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message(Message.BULLETIN_FOUND.label())
                    .data(responseData)
                    .build();
        } else {
            restResponse = RestResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message(Message.BULLETIN_NOT_FOUND.label())
                    .build();
        }

        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @GetMapping("bulletin/prediction/{id}")
    public ResponseEntity<RestResponse> getAIPrediction(@PathVariable("id") Long id) {
        RestResponse<Object> restResponse = new RestResponse<>();
        Optional<Bulletin> bulletin = bulletinService.findById(id);

        Optional<Map<String, String>> aiPrediction = bulletinService.sendRequestToAIServer(bulletin.get().getImageFilePath());

        if (aiPrediction.isEmpty()) {
            restResponse = RestResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(Message.BULLETIN_AI_PREDICTION_FAILED.label())
                    .build();
        }

        else {
            String aiPredictionStringFormat = getAiPredictionAsStringFormat(aiPrediction.get());

            restResponse = RestResponse.builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message(aiPredictionStringFormat)
                    .data(bulletin.get())
                    .build();
        }

        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @PostMapping("/bulletin/save")
    public ResponseEntity<RestResponse> saveBulletin(@RequestBody Bulletin bulletin) {
        this.bulletinService.save(bulletin);
        RestResponse<Object> restResponse = new RestResponse<>();

        restResponse = RestResponse.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message(Message.BULLETIN_SAVE_SUCCESS.label())
                .build();
        return new ResponseEntity<>(restResponse, HttpStatus.CREATED);
    }

    @PostMapping("/bulletin/save/image")
    public ResponseEntity<RestResponse> saveBulletinWithImage(@RequestPart("bulletin") Bulletin bulletin,
                                                              @RequestPart("file") MultipartFile file) throws Exception {

        RestResponse<Object> restResponse = new RestResponse<>();

        try {
            String imageUrl = s3Uploader.upload(file, "bulletin"); // "bulletin"은 S3 내 폴더
            bulletin.setImageFilePath(imageUrl); // URL을 경로로 설정
            bulletinService.save(bulletin);

            restResponse = RestResponse.builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message(Message.BULLETIN_SAVE_SUCCESS.label())
                    .build();

            return new ResponseEntity<>(restResponse, HttpStatus.CREATED);
        } catch (Exception e) {
            restResponse = RestResponse.builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(Message.BULLETIN_SAVE_FAILED.label())
                    .build();

            return new ResponseEntity<>(restResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/bulletin/modify/{id}")
    public ResponseEntity<RestResponse> modifyBulletin(@PathVariable Long id, @RequestBody Bulletin newBulletin) {
        Optional<Bulletin> bulletin = bulletinService.findById(id);

        RestResponse<Object> restResponse = new RestResponse<>();

        if (bulletin.isEmpty()) {
            restResponse = RestResponse.builder()
                    .code(HttpStatus.NOT_MODIFIED.value())
                    .httpStatus(HttpStatus.NOT_MODIFIED)
                    .message(Message.BULLETIN_MODIFY_FAILED.label())
                    .build();
            return new ResponseEntity<>(restResponse, HttpStatus.NOT_FOUND);
        }

        bulletin = changeBulletin(bulletin, newBulletin);
        bulletinService.merge(bulletin.get());

        restResponse = RestResponse.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message(Message.BULLETIN_MODIFY_SUCCESS.label())
                .data(bulletin.get())
                .build();

        return new ResponseEntity<>(restResponse, HttpStatus.OK);

    }

    @DeleteMapping("/bulletin/delete/{id}")
    public ResponseEntity<RestResponse> deleteBulletin(@PathVariable Long id) {
        Optional<Bulletin> bulletin = bulletinService.findById(id);

        RestResponse<Object> restResponse = new RestResponse<>();

        if (!bulletin.isPresent()) {
            restResponse = RestResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message(Message.BULLETIN_DELETE_FAILED.label())
                    .build();
            return new ResponseEntity<>(restResponse, HttpStatus.NOT_FOUND);
        }

        bulletinService.deleteById(id);

        restResponse = RestResponse.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message(Message.BULLETIN_DELETE_SUCCESS.label())
                .build();

        return new ResponseEntity<>(restResponse, HttpStatus.OK);


    }

    public String extractKeyFromUrl(String s3Url) {
        // https://your-bucket.s3.amazonaws.com/images/profile/user123.jpg
        return s3Url.substring(s3Url.indexOf(".com/") + 5); // "images/profile/user123.jpg"
    }

    public static Optional<Bulletin> changeBulletin(Optional<Bulletin> baseBulletin, Bulletin newBulletin) {
        Bulletin bulletin = baseBulletin.get();

        bulletin.setLabel(newBulletin.getLabel());
        bulletin.setTitle(newBulletin.getTitle());
        bulletin.setImageFilePath(newBulletin.getImageFilePath());

        return Optional.of(bulletin);
    }

    public Map<String, String> convertHttpServletRequestToMap(HttpServletRequest httpServletRequest) {
        Map<String, String> map = new HashMap<>();

        try (BufferedReader reader = httpServletRequest.getReader()) {
            StringBuilder requestBody = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }


            ObjectMapper objectMapper = new ObjectMapper();

            map = objectMapper.readValue(requestBody.toString(), HashMap.class);


        } catch (IOException e) {
            // Handle IOException appropriately
            e.printStackTrace();
        }

        return map;
    }

    public String getAiPredictionAsStringFormat(Map<String, String> inputMap) {
        return inputMap.entrySet()
                .stream()
                .filter(entry -> "1".equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
    }
}
