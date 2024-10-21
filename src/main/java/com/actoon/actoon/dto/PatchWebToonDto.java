package com.actoon.actoon.dto;

import com.actoon.actoon.domain.Webtoon;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PatchWebToonDto{

    int uuid;
    String userId;
    String content;
    int style;
    String tags;

    @Builder
    public Webtoon toEntity(){
        return Webtoon.builder()
                .content(content)
                .style(style)
                .tags(tags)
                .build();
    }
}