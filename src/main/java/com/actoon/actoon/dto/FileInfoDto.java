package com.actoon.actoon.dto;

import com.actoon.actoon.domain.FileInfoRegister;
import com.actoon.actoon.domain.Chain;
import lombok.*;

public class FileInfoDto {

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileInfoResponseDto {
        private Integer fileId;
        private String url;
 

        public static FileInfoResponseDto of(FileInfoRegister fileInfo) {
            return FileInfoResponseDto.builder()
                    .fileId(fileInfo.getFileId())
                    .url(fileInfo.getUrl())
                    .build();
        }

        // Chain 정보를 포함하여 DTO를 생성하는 메서드
        public static FileInfoResponseDto of(FileInfoRegister fileInfo, Chain chain) {
            return FileInfoResponseDto.builder()
                    .fileId(fileInfo.getFileId())
                    .url(fileInfo.getUrl())
                    .build();
        }
    }
}