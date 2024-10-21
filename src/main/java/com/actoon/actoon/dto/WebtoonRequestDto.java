package com.actoon.actoon.dto;

import com.actoon.actoon.domain.Webtoon;
import lombok.*;

public class WebtoonRequestDto {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class WebtoonCreateRequestDto {

        int style;

        String title;
        String content;
        String tags;
        Integer fileId;
        // 추가
        Integer count;

        @Builder
        public Webtoon toEntity(){
            return Webtoon.builder()
                    .title(title)
                    .content(content)
                    .style(style)
                    .tags(tags)
                    .fileId(fileId)
                    .count(count) //추가
                    .build();
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WebtoonStateChangeDto{

        String progressState;
    }

}
