package com.actoon.actoon.dto;

import java.util.List;

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

    @Getter
    @Setter
    @AllArgsConstructor
    public static class NoticeBoardUpdateRequestDTO {

        private String title;
        private String content;
        private Integer fileId; 

        // Getters and Setters
        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Integer getFileId() {
            return fileId;
        }

        public void setFileIds(Integer fileId) {
            this.fileId = fileId;
        }
    }
}