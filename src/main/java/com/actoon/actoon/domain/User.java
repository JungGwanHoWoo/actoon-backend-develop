package com.actoon.actoon.domain;

import com.actoon.actoon.util.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Table(name="Users")
@Entity
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uuid;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = true)
    private String nickname;

    @Column(nullable = true)
    private String birthday;

    @Column(name = "\"fileid\"")
    private Integer fileId;

    //@Column(nullable = false)// DATE
    @Column(name = "\"created_at\"")
    private String created_at; // DATE

    @Column(name = "\"last_change\"")
    @UpdateTimestamp
    private String last_change;

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @Column(name = "\"profile\"")
    private String profile;

    @Builder
    public User(String password, String nickname, String email, String birthday, Role role, String profile){
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.birthday = birthday;
        this.role = role;
        this.profile = profile;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println(role.name());
        return List.of(new SimpleGrantedAuthority(role.name())); // ROLE_이 앞에 띄워지나?
    }

    @Override
    public String getPassword() {
        return password;
    }

    // 아이디 반환
    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }


}
