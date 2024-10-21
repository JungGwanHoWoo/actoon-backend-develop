package com.actoon.actoon.dto;

import com.actoon.actoon.domain.WebtoonFileInfo;
import com.actoon.actoon.domain.NoticeBoardFileInfo;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

public class FileDto {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CompleteFilesInfoDto {

        String zip_url;
        String pdf_url;
        String img_url;

        public static CompleteFilesInfoDto toDto(WebtoonFileInfo webtoons) {
            return CompleteFilesInfoDto
                    .builder()
                    .img_url(webtoons.getImg_url())
                    .pdf_url(webtoons.getPdf_url())
                    .zip_url(webtoons.getZip_url())
                    .build();

        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CompleteNoticeBoardFilesInfoDto {

        String zip_url;
        String pdf_url;
        String img_url;

        public static CompleteFilesInfoDto toDto(NoticeBoardFileInfo noticeBoards) {
            return CompleteFilesInfoDto
                    .builder()
                    .img_url(noticeBoards.getImg_url())
                    .build();

        }

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompleteFileRequestDto {

        MultipartFile zip_file;
        MultipartFile pdf_file;
        MultipartFile img_file;

    }

}
