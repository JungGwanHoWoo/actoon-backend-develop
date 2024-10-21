package com.actoon.actoon.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class JwtAuthenticationResponse{

    @Getter
    public static class ReissueJwtAuthenticaiton{
        HttpStatus state;
        String message;
        String accessToken;

        @Builder
        public ReissueJwtAuthenticaiton(HttpStatus state, String message, String accessToken){
            this.state = state;
            this.message = message;
            this.accessToken = accessToken;
        }
    }

    @Getter
    public static class IssueJwtAuthentication{
        HttpStatus state;
        String message;
        String accessToken;
        String refreshToken;

        @Builder
        public IssueJwtAuthentication(HttpStatus state, String message, String accessToken, String refreshToken){
            this.state = state;
            this.message = message;
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }

}
