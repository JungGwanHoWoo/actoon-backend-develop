package com.actoon.actoon.repository;


import com.actoon.actoon.domain.EmailAuth;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface EmailRedisRepository extends CrudRepository<EmailAuth, Integer> {
//
    Optional<EmailAuth> findById(int key);

    Optional<EmailAuth> findByUuid(int key);


}
