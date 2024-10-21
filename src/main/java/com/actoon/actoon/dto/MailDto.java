package com.actoon.actoon.dto;

import com.actoon.actoon.domain.EmailAuth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
public class MailDto {

    String email;
    int uuid;
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    public static class AuthenticateMailDto{
        String email;
        int number;
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class EmailAuthDto{
        String email;
        int number;
        int uuid;

        public EmailAuth toEntity(){
            return EmailAuth
                    .builder()
                    .email(email)
                    .number(number)
                    .uuid(uuid)
                    .build();
        }
    }

}

