package com.actoon.actoon.dto;

import com.actoon.actoon.domain.NoticeBoard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class NoticeBoardRequestDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class NoticeBoardCreateRequestDTO {

        int style;

        String title;
        String content;
        Integer fileId;

        @Builder
        public NoticeBoard toEntity(){
            return NoticeBoard.builder()
                    .title(title)
                    .content(content)
                    .fileId(fileId)
                    .build();
        }

    }
}
