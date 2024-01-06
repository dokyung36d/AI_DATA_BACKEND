package com.example.AI_DATA;

import com.example.AI_DATA.bulletin.Service.BulletinService;
import com.example.AI_DATA.bulletin.model.Bulletin;

import com.example.AI_DATA.restapi.Message;
import com.example.AI_DATA.restapi.RestResponse;
import lombok.val;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.*;

@RestController
public class ApiController {
    private final BulletinService bulletinService;

    @Autowired
    public ApiController(BulletinService bulletinService) {
        this.bulletinService = bulletinService;
    }

    @GetMapping("/bulletin/view/{id}")
    public ResponseEntity<RestResponse> find(@PathVariable Long id) {
        RestResponse<Object> restResponse = new RestResponse<>();
        Optional<Bulletin> bulletin = bulletinService.findById(id);

        if (bulletin != null) {
            restResponse = RestResponse.builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message(Message.BULLETIN_FOUND.label())
                    .data(bulletin)
                    .build();
        }

        else {
            restResponse = RestResponse.builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .message(Message.BULLETIN_NOT_FOUND.label())
                    .build();
        }

        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @PostMapping("bulletin/save")
    public ResponseEntity<RestResponse> save(@RequestBody Bulletin bulletin) {
        this.bulletinService.save(bulletin);
        RestResponse<Object> restResponse = new RestResponse<>();

        restResponse = RestResponse.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message(Message.BULLETIN_SAVE_SUCCESS.label())
                .build();
        return new ResponseEntity<>(restResponse, HttpStatus.CREATED);
    }


    @PutMapping("bulletin/modify/{id}")
    public ResponseEntity<RestResponse> modify(@PathVariable Long id, @RequestBody Bulletin newBulletin) {
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
        bulletinService.save(bulletin.get());

        restResponse = RestResponse.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message(Message.BULLETIN_MODIFY_SUCCESS.label())
                .build();

        return new ResponseEntity<>(restResponse, HttpStatus.OK);

    }

    @DeleteMapping("bulletin/delete/{id}")
    public ResponseEntity<RestResponse> delete(@PathVariable Long id) {
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
        bulletin.setImageData(newBulletin.getImageData());

        return Optional.of(bulletin);
    }
}
