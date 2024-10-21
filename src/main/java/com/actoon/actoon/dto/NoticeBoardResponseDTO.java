package com.actoon.actoon.dto;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 공지사항 관련 응답 DTO
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NoticeBoardResponseDTO {

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

    // 공지사항 읽기 응답 DTO
    @Setter
    @Getter
    public static class ReadResponseDto {

        HttpStatus state;
        String message;
        List<NoticeBoardInfoDTO> datas;

        int totalPages;
        long totalElements;
        int maximumElementsPerPage;

        boolean last;

        public ReadResponseDto() {
        }

        public ReadResponseDto(HttpStatus state, String message, List<NoticeBoardInfoDTO> datas) {
            this.state = state;
            this.message = message;
            this.datas = datas;
        }
    }
    

    // 공지사항 생성 응답 DTO
    @Setter
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoticeBoardStateResponseDto {
        int total;
        HttpStatus state;
        String message;
    }
}
