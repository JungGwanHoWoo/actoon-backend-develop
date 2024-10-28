package com.actoon.actoon.dto;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class UploadFileDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UploadFileRequestDto {
        private List<MultipartFile> files; // 여러 파일을 받을 수 있도록 변경
        MultipartFile file;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UploadCompleteFileRequestDto {
        MultipartFile file;
        String type;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UploadFileResponseDto { // 응답으로 준다.
        HttpStatus state;
        String message;
        int uuid;
        String url;
    }

}
