package com.actoon.actoon.domain;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="files")
public class FileInfoRegister {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "\"fileid\"")
    Integer fileId;
    String url;
    String created_at;

    @Column(name = "\"userid\"")
    int userId;
    boolean deleteFlag;

    @Override
    public String toString(){
        return "[" + fileId + " | " + url + " | " + created_at + " | " + userId + " | " + deleteFlag + "]";
    }

}
