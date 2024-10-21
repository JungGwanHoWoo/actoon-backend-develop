package com.actoon.actoon.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Table(name="notice_board_files")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class NoticeBoardFileInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int uuid;

    @Column(name = "\"noticeboardid\"")
    Integer noticeBoardId;

    String img_url;

    

    @CreationTimestamp
    String created_at;

    @UpdateTimestamp
    String updated_at;


}
