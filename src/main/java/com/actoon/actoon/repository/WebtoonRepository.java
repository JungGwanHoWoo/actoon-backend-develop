package com.actoon.actoon.repository;

import com.actoon.actoon.domain.Webtoon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface WebtoonRepository extends JpaRepository<Webtoon, Integer> {

    //public Optional<Webtoon> findById(int id);
    public Page<Webtoon> findAll(Pageable pageable);

    public Webtoon findByUuid(int webtoonId);

    public Webtoon findByFileId(int fileId);
    public List<Webtoon> findByUserId(int userId);
    public Page<Webtoon> findByUserId(int userId, PageRequest pageable);

    public Page<Webtoon> findByUserIdAndProgress(int userId, int progress, PageRequest pageable);

    // case when으로 정렬 우선순위를 지정한다.
    @Query(value = "SELECT * FROM Webtoons w " +
            "WHERE w.userId = :userId " +
            "ORDER BY (" +
            "CASE WHEN (w.is_read = false AND progress = 0) THEN 0 " +
            "ELSE 1 " +
            "END" +
            "), w.uuid DESC", nativeQuery = true)
    public Page<Webtoon> findWebtoonWithPriorityWithoutFilter(@Param("userId") int userId, Pageable pageable);

    @Query(value = "SELECT * FROM Webtoons w " +
            "WHERE w.userId = :userId AND progress = :progress " +
            "ORDER BY (" +
            "CASE WHEN (w.is_read = false AND progress = 0) THEN 0 " +
            "ELSE 1 " +
            "END" +
            "), w.uuid DESC", nativeQuery = true)
    public Page<Webtoon> findWebtoonWithPriorityWithFilter(@Param("userId") int userId, @Param("progress") int progress, Pageable pageable);

    //public Page<Webtoon> findByUuid(Pageable pageable);
    public List<Webtoon> findByOrderByUuidDesc();

}
