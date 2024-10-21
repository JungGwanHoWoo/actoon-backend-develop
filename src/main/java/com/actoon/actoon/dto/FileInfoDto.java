package com.actoon.actoon.dto;


import com.actoon.actoon.domain.FileInfoRegister;
import lombok.*;

public class FileInfoDto {

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FileInfoResponseDto{
        Integer fileId;
        String url;
        String created_at;


        public static FileInfoResponseDto of(FileInfoRegister fileInfo){
            return FileInfoResponseDto
                    .builder()
                    .fileId(fileInfo.getFileId())
                    .url(fileInfo.getUrl())
                    .created_at(fileInfo.getCreated_at())
                    .build();

        }
    }
}
