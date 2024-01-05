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
        Optional<Bulletin> bulletin = bulletinService.findByid(id);

        if (bulletin.isPresent()) {
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
                    .data(bulletin)
                    .build();
        }

        return new ResponseEntity<>(restResponse, restResponse.getHttpStatus());
    }

    @PostMapping("bulletin/save")
    public ResponseEntity<String> save(@RequestBody Bulletin bulletin) {
        this.bulletinService.save(bulletin);
        return new ResponseEntity<>("Bulletin saved with username: " + bulletin.getTitle(), HttpStatus.CREATED);
    }


    @PutMapping("bulletin/modify/{id}")
    public ResponseEntity<String> modify(@PathVariable Long id, @RequestBody Bulletin newBulletin) {
        Bulletin bulletin = bulletinService.findByid(id);

        if (!bulletin.isPresent()) { return new ResponseEntity<>("Bulletin Not Found", HttpStatus.NOT_FOUND);}

        bulletin = changeBulletin(bulletin, newBulletin);
        this.bulletinService.save(bulletin.get());

        return new ResponseEntity<>("bulletin modified with id : " + bulletin.get().getId() + bulletin.get().getLabel(), HttpStatus.OK);

    }

    @DeleteMapping("bulletin/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        Optional<Bulletin> bulletin = this.bulletinService.findByid(id);

        if (!bulletin.isPresent()) {
            return new ResponseEntity<>("No Bulletin Found", HttpStatus.NOT_FOUND);
        }
        this.bulletinService.deleteById(id);

        return new ResponseEntity<>("Delete Successful", HttpStatus.OK);


    }

    public static Optional<Bulletin> changeBulletin(Optional<Bulletin> baseBulletin, Bulletin newBulletin) {
        Bulletin bulletin = baseBulletin.get();

        bulletin.setLabel(newBulletin.getLabel());
        bulletin.setTitle(newBulletin.getTitle());
        bulletin.setImageData(newBulletin.getImageData());

        return Optional.of(bulletin);
    }
}
