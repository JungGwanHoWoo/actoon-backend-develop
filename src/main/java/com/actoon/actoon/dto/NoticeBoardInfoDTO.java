package com.actoon.actoon.dto;

import com.actoon.actoon.domain.NoticeBoard;
import com.actoon.actoon.dto.FileInfoDto.FileInfoResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeBoardInfoDTO {

    private int uuid;
    private int userId;

    private Integer fileId;
    private String title;
    private String content;

    UserDto.UserInfo user;
    FileInfoResponseDto fileInfo;
    private String created_at;

    private boolean is_read;

    FileDto.CompleteNoticeBoardFilesInfoDto noticeBoardFiles;

    public void setFileInfo(FileInfoResponseDto file) {
        this.fileInfo = file;
    }

    public static NoticeBoardInfoDTO of(NoticeBoard noticeBoard) {
        return NoticeBoardInfoDTO.builder()
                .uuid(noticeBoard.getUuid())
                .userId(noticeBoard.getUserId())
                .fileId(noticeBoard.getFileId())
                .title(noticeBoard.getTitle())
                .content(noticeBoard.getContent())
                .created_at(noticeBoard.getCreatedAt())
                .is_read(noticeBoard.isRead())
                .build();
    }

    public void setNoticeBoard(NoticeBoard noticeBoard) {
        this.uuid = noticeBoard.getUuid();
        this.userId = noticeBoard.getUserId();
        this.fileId = noticeBoard.getFileId();
        this.title = noticeBoard.getTitle();
        this.content = noticeBoard.getContent();
        this.created_at = noticeBoard.getCreatedAt();
        this.is_read = noticeBoard.isRead();

    }

    public void setUser(UserDto.UserInfo user) {
        this.user = user;
    }

    public void setCompleteFiles(FileDto.CompleteNoticeBoardFilesInfoDto noticeBoardFiles) {
        this.noticeBoardFiles = noticeBoardFiles;
    }

    public void setCompleteFiles(FileDto.CompleteFilesInfoDto noticeBoardFiles) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
