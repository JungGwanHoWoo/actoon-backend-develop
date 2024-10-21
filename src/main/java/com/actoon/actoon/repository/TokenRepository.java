package com.actoon.actoon.repository;

import com.actoon.actoon.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface TokenRepository extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findById(String refreshToken);
}
