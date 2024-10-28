package com.actoon.actoon.repository;

import com.actoon.actoon.domain.FileInfoRegister;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import com.actoon.actoon.domain.NoticeBoardFileInfo;

@Repository
public interface FileUploadRepository extends JpaRepository<FileInfoRegister, Integer> {

    // 특정 사용자의 모든 파일 정보 가져오기
    List<FileInfoRegister> findByUserId(int userId);

    // 특정 파일 ID로 파일 정보 가져오기 (단일 결과로 유지 시 OK)
    FileInfoRegister findByFileId(int fileId);

    public void save(NoticeBoardFileInfo fileInfoRegister);

}
