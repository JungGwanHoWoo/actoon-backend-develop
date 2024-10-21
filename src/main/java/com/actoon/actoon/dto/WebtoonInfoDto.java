package com.actoon.actoon.dto;


import com.actoon.actoon.dto.FileInfoDto.FileInfoResponseDto;
import com.actoon.actoon.domain.Webtoon;
import com.actoon.actoon.util.WebtoonProgressState;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WebtoonInfoDto{

    int uuid;
    int userId;

    Integer fileId;
    String title;
    String content;
    List<String> tags;

    boolean successFlag;
    UserDto.UserInfo user;
    FileInfoResponseDto fileInfo;
    String created_at;

    // 추가
    Integer style;
    Integer count;
    Boolean is_read;

    String progress_state;

    FileDto.CompleteFilesInfoDto webtoonFiles;


    public static List<String> split(String tags){

        String[] splitedTag = tags.split("#");
        List<String> taglist = new ArrayList<>();

        for(String t : splitedTag){ // 안정적인 부분으로 고쳐야한다!
            if(t.equals("")) continue;
            taglist.add("#" + t);
            System.out.println("#" + t);
        }
        return taglist;
    }
    //
    public void setFileInfo(FileInfoResponseDto file){
        this.fileInfo = file;
    }

    public static WebtoonInfoDto of(Webtoon webtoon){
        int progress = webtoon.getProgress();
        String state = WebtoonProgressState.getState(progress);
        System.out.println("STATE STR : " + state);
        return WebtoonInfoDto.builder()
                .userId(webtoon.getUserId())
                .fileId(webtoon.getFileId())
                .uuid(webtoon.getUuid())
                .title(webtoon.getTitle())
                .content(webtoon.getContent())
                .count(webtoon.getCount())
                .tags(split(webtoon.getTags()))
                .style(webtoon.getStyle())
                .successFlag(webtoon.isSuccessFlag())
                .created_at(webtoon.getCreatedAt())
                .is_read(webtoon.isRead())
                .progress_state(state)
                .build();
    }

    public void setWebtoon(Webtoon webtoon){
        this.userId = webtoon.getUserId();
        this.fileId = webtoon.getFileId();
        this.title = webtoon.getTitle();
        this.content = webtoon.getContent();
        this.uuid = webtoon.getUuid();
        this.successFlag = webtoon.isSuccessFlag();
        this.created_at = webtoon.getCreatedAt();
        this.tags = split(webtoon.getTags());
        this.style = webtoon.getStyle();
        this.count = webtoon.getCount();

        int progress = webtoon.getProgress(); // int
        String progress_state = WebtoonProgressState.getState(progress);
        this.progress_state = progress_state;
    }

    public void setUser(UserDto.UserInfo user){
        this.user = user;
    }

    public void setCompleteFiles(FileDto.CompleteFilesInfoDto webtoonFiles){
        this.webtoonFiles = webtoonFiles;
    }

}
