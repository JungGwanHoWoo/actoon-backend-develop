package com.actoon.actoon.dto;

import org.springframework.http.HttpStatus;

public class NonMemberDto {

    public static class NonMemberRequestDto{
        String email;
    }

    public static class NonMemberResponseDto{
        HttpStatus state;
        String message;
    }

}
