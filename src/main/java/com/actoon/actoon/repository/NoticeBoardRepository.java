package com.actoon.actoon.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.actoon.actoon.domain.NoticeBoard;

@Repository
public interface NoticeBoardRepository extends JpaRepository<NoticeBoard, Integer> {

    public Page<NoticeBoard> findAll(Pageable pageable);

    public NoticeBoard findByUuid(int noticeBoardId);

    public NoticeBoard findByFileId(int fileId);

    public List<NoticeBoard> findByUserId(int userId);

    public Page<NoticeBoard> findByUserId(int userId, PageRequest pageable);

    // 사용자가 작성한 공지사항을 읽지 않은 것과 진행 상황에 따라 우선순위를 지정하여 조회
    public Page<NoticeBoard> findByUserIdAndIsRead(int userId, boolean isRead, PageRequest pageable);

    // 우선순위를 고려하여 정렬된 공지사항을 조회
    @Query(value = "SELECT * FROM NoticeBoard n " +
            "WHERE n.userId = :userId " +
            "ORDER BY (" +
            "CASE WHEN (n.is_read = false) THEN 0 " +
            "ELSE 1 " +
            "END" +
            "), n.uuid DESC", nativeQuery = true)
    public Page<NoticeBoard> findNoticeBoardWithPriorityWithoutFilter(@Param("userId") int userId, Pageable pageable);

    @Query(value = "SELECT * FROM NoticeBoard n " +
            "WHERE n.userId = :userId AND n.is_read = :isRead " +
            "ORDER BY (" +
            "CASE WHEN (n.is_read = false) THEN 0 " +
            "ELSE 1 " +
            "END" +
            "), n.uuid DESC", nativeQuery = true)
    public Page<NoticeBoard> findNoticeBoardWithPriorityWithFilter(@Param("userId") int userId, @Param("isRead") boolean isRead, Pageable pageable);

    public List<NoticeBoard> findByOrderByUuidDesc();
}
