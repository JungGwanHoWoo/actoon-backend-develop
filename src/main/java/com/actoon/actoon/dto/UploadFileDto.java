package com.actoon.actoon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

public class UploadFileDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UploadFileRequestDto{
        MultipartFile file;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UploadCompleteFileRequestDto{
        MultipartFile file;
        String type;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UploadFileResponseDto{ // 응답으로 준다.
        HttpStatus state;
        String message;
        int uuid;
        String url;
    }


}
