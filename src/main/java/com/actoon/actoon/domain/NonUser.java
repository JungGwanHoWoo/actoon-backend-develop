package com.actoon.actoon.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(name="\"NonUsers\"")
@Entity
@NoArgsConstructor
@Getter
@Setter
public class NonUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int uuid;

    String email;
    String created_at;


    @Builder
    public NonUser(String email, String created_at){
        this.email = email;
        this.created_at = created_at;
    }

}
