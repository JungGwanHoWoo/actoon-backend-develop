package com.actoon.actoon.repository;

import com.actoon.actoon.domain.NonUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NonUserRepository extends CrudRepository<NonUser, Integer> {

    public Page<NonUser> findAll(Pageable pageable);

}
