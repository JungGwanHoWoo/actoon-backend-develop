package com.actoon.actoon.dto;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WebtoonResponseDto {
    HttpStatus status;
    String message;
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class createResponseDto{
        HttpStatus status;
        String message;
    }


    @Getter
    @Setter
    //@AllArgsConstructor
    public static class ReadResponseDto{
        HttpStatus state;
        String message;
        List<WebtoonInfoDto> datas;

        int totalPages;
        long totalElements;
        int maximumElementsPerPage;

        boolean last;


        public ReadResponseDto(){}

        public ReadResponseDto(HttpStatus state, String message, List<WebtoonInfoDto> datas){
            this.state = state;
            this.message = message;
            this.datas = datas;
        }
    }

    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WebtoonStateResponseDto{
        HttpStatus state;
        String message;
        int rejected;
        int continued;
        int completed;
        int total;
    }


}

