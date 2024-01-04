package com.example.AI_DATA.restapi;

import com.example.AI_DATA.bulletin.model.Bulletin;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse<T> {
    private Integer code;
    private HttpStatus httpStatus;
    private String message;
    private T data;
}
