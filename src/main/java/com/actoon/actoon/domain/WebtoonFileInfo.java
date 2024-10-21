package com.actoon.actoon.domain;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Table(name="webtoon_files")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class WebtoonFileInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int uuid;

    @Column(name = "\"webtoonid\"")
    Integer webtoonId;

    String zip_url;
    String pdf_url;
    String img_url;

    @CreationTimestamp
    String created_at;

    @UpdateTimestamp
    String updated_at;


}
