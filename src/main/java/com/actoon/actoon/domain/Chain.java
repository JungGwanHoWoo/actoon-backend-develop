package com.actoon.actoon.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "\"Chain\"")
@Entity
@NoArgsConstructor
@Getter
@Setter
public class Chain {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"id\"") // id는 자동 생성됨
    private Integer id; // 새로운 필드 추가

    @Column(name = "\"fileid\"")
    private Integer fileId;

    @Column(name = "\"boardid\"")
    private Integer boardId;

     @Builder
    public Chain(Integer fileId, Integer boardId){
        this.fileId = fileId;
        this.boardId = boardId;
    }


}
