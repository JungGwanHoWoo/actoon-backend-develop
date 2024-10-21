package com.actoon.actoon.repository;


import com.actoon.actoon.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

// DAO 로 repository를 사용
public interface UserRepository extends JpaRepository<User, Integer> {
    public Optional<User> findByEmail(String email);
    public Optional<User> findByNickname(String nickname);

    public Optional<User> findByUuid(int userId);
    public List<User> findAll();
}
