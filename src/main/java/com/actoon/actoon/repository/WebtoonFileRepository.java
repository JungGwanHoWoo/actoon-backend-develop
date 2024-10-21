package com.actoon.actoon.repository;

import com.actoon.actoon.domain.WebtoonFileInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebtoonFileRepository extends JpaRepository<WebtoonFileInfo, Integer> {

    public WebtoonFileInfo findByWebtoonId(int webtoonId);

    public List<WebtoonFileInfo> findAll();

}
