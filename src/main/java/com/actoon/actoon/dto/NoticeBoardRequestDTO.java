package com.actoon.actoon.dto;

import java.util.List;
import com.actoon.actoon.domain.NoticeBoard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class NoticeBoardRequestDTO {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class NoticeBoardCreateRequestDTO {
        int style;
        String title;
        String content;
        List<Integer> fileIds;

        @Builder
        public NoticeBoard toEntity() {
            return NoticeBoard.builder()
                    .title(title)
                    .content(content)
                    .fileId(fileIds.isEmpty() ? null : fileIds.get(0))
                    .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoticeBoardUpdateRequestDTO {  
        private String title;
        private String content;
        private List<Integer> fileIds;
    }
}