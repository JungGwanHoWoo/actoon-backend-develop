package com.actoon.actoon.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(name="Webtoons")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Webtoon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uuid;

    @Column(name = "\"fileid\"")
    private Integer fileId;

    private String title; // 이거 추가 예정

    private String content;

    private int style; // 그림체
    private String tags;

    @Column(name = "\"userid\"")
    private int userId;

    @Column(name = "\"created_at\"")
    private String createdAt;

    @Column(name = "\"successflag\"")
    private boolean successFlag;

    // 추가
    private Integer count;
    private int progress;

    @Column(name = "\"is_read\"")
    private boolean isRead;


    @Builder
    public Webtoon(String title, String content, int style, String tags, Integer fileId, Integer count, Integer progress){
        this.title = title;
        this.content = content;
        this.style = style;
        this.tags = tags;
        this.fileId = fileId;
        this.count = count;
        this.progress = progress;
    }

}
