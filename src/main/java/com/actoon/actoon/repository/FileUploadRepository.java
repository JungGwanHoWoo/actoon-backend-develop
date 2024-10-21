package com.actoon.actoon.repository;

import com.actoon.actoon.domain.FileInfoRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileUploadRepository extends JpaRepository<FileInfoRegister, Integer> {

    FileInfoRegister findByUserId(int userId);

    FileInfoRegister findByFileId(int fileId);

}
