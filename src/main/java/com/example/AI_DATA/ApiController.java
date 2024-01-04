package com.example.AI_DATA;

import com.example.AI_DATA.bulletin.Service.BulletinService;
import com.example.AI_DATA.bulletin.model.Bulletin;

import com.example.AI_DATA.restapi.Message;
import com.example.AI_DATA.restapi.RestResponse;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
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

    //@GetMapping("bulletein/save/)

}
