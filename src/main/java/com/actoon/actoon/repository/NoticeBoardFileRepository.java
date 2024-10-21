package com.actoon.actoon.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.actoon.actoon.domain.NoticeBoardFileInfo;

public interface NoticeBoardFileRepository extends JpaRepository<NoticeBoardFileInfo, Integer> {

    public NoticeBoardFileInfo findByNoticeBoardId(int noticeBoardId);

    public List<NoticeBoardFileInfo> findAll();

}
