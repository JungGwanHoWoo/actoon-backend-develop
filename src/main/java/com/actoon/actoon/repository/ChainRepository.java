package com.actoon.actoon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.actoon.actoon.domain.Chain;

@Repository
public interface ChainRepository extends JpaRepository<Chain, Integer> {

    List<Chain> findByFileId(int fileId); // fileId로 Chain 객체 가져오기

    List<Chain> findByBoardId(int boardId); // 특정 boardId로 Chain 객체 가져오기

    @Transactional
    @Modifying
    @Query("DELETE FROM Chain c WHERE c.boardId = :boardId")
    void deleteByBoardId(int boardId);

    List<Chain> findAll(); // 모든 Chain 객체 가져오기
}
