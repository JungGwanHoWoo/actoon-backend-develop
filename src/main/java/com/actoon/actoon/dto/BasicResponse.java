package com.actoon.actoon.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BasicResponse {
    HttpStatus status;
    String message;

    @Builder
    public BasicResponse(HttpStatus state, String message) {
        this.status = state;
        this.message = message;
    }
}
