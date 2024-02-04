package com.example.AI_DATA;

import com.example.AI_DATA.bulletin.Service.BulletinService;
import com.example.AI_DATA.bulletin.model.Bulletin;

import com.example.AI_DATA.restapi.Message;
import com.example.AI_DATA.restapi.RestResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.Part;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
public class BulletinApiController {
    private final BulletinService bulletinService;

    @Autowired
    public BulletinApiController(BulletinService bulletinService) {
        this.bulletinService = bulletinService;
    }

    @GetMapping("/bulletin/view/{id}")
    public ResponseEntity<RestResponse> findBulletin(@PathVariable("id") Long id) {
        RestResponse<Object> restResponse = new RestResponse<>();
        Optional<Bulletin> bulletin = bulletinService.findById(id);

        if (!bulletin.isEmpty()) {
            restResponse = RestResponse.builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message(Message.BULLETIN_FOUND.label())
                    .data(bulletin.get())
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
    public ResponseEntity<RestResponse> saveBulletinWithImage(HttpServletRequest request) throws Exception {
        Map<String, String> map = new HashMap<>();
        map = convertHttpServletRequestToMap(request);

        RestResponse<Object> restResponse = new RestResponse<>();

        Part jpgFilePart = request.getPart("file");
        String fileName = jpgFilePart.getSubmittedFileName();

        Path targetPath = Path.of("C:\\Users\\dokyu\\OneDrive - UOS\\바탕 화면\\AI_DATA\\image" + fileName);

        try (InputStream fileContent = jpgFilePart.getInputStream()) {
            Files.copy(fileContent, targetPath, StandardCopyOption.REPLACE_EXISTING);

            Bulletin bulletin = new Bulletin(map.get("title").toString(), map.get("label").toString(), targetPath.toString());
            this.bulletinService.save(bulletin);

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
}
