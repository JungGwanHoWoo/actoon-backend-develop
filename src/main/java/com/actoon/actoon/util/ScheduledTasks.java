package com.actoon.actoon.util;


import com.actoon.actoon.domain.WebtoonFileInfo;
import com.actoon.actoon.repository.WebtoonFileRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;


@Slf4j
@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    WebtoonFileRepository webtoonFileRepository;

    // 오전 0시

    // 오전 0시마다 삭제하도록 구현

    // 꼭 변경!!!
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deleteFiles() throws ParseException {
        logger.info("[SCHEDULER] START - 14일 경과된 파일 삭제");
        List<WebtoonFileInfo> webtoonFileInfos = webtoonFileRepository.findAll();

        for(WebtoonFileInfo infos : webtoonFileInfos){

            String updatedAt = infos.getUpdated_at();
            System.out.println(updatedAt);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            LocalDate updated = LocalDate.parse(updatedAt, formatter);
            LocalDate now = LocalDate.now();

            long days = DAYS.between(updated, now);
            System.out.println("마지막 업데이트로부터 현재까지의 날짜 차이 : " + days);

            LocalDateTime nowTime = LocalDateTime.now();
            String sNow = nowTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));


            if(days >= 14) {
                infos.setZip_url(null);
                infos.setPdf_url(null);
                infos.setUpdated_at(sNow);

                logger.info("[SCHEDULER] INFO - 14일 경과된 pdf, zip 삭제");
            }

        }

        logger.info("[SCHEDULER] END - 14일 경과된 파일 삭제");
    }


}
