package com.actoon.actoon.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(name="NoticeBoard")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NoticeBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uuid;

    @Column(name = "\"fileid\"")
    private Integer fileId;

    private String title;

    private String content;

    @Column(name = "\"userid\"")
    private int userId;

    @Column(name = "\"created_at\"")
    private String createdAt;

    @Column(name = "\"is_read\"")
    private boolean isRead;


    @Builder
    public NoticeBoard(String title, String content, Integer fileId){
        this.title = title;
        this.content = content;
        this.fileId = fileId;
    }

}
