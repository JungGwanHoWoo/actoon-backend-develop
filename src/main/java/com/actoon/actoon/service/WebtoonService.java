package com.actoon.actoon.service;

import com.actoon.actoon.domain.FileInfoRegister;
import com.actoon.actoon.domain.Webtoon;
import com.actoon.actoon.domain.WebtoonFileInfo;
import com.actoon.actoon.dto.FileDto.*;
import com.actoon.actoon.dto.FileInfoDto.*;
import com.actoon.actoon.dto.UserDto.*;
import com.actoon.actoon.dto.WebtoonRequestDto.*;
import com.actoon.actoon.dto.WebtoonInfoDto;
import com.actoon.actoon.dto.WebtoonResponseDto.*;
import com.actoon.actoon.exception.ErrorCode;
import com.actoon.actoon.repository.FileUploadRepository;
import com.actoon.actoon.repository.UserRepository;
import com.actoon.actoon.repository.WebtoonFileRepository;
import com.actoon.actoon.repository.WebtoonRepository;
import com.actoon.actoon.util.WebtoonProgressState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.NoPermissionException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


// 웹툰에 관련한 요청을 처리하는 서비스 로직
@Service
public class WebtoonService {

    @Autowired
    WebtoonRepository webtoonRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FileUploadRepository fileRepository;

    @Autowired
    WebtoonFileRepository webtoonFileRepository;

    private static final Logger logger = LoggerFactory.getLogger(WebtoonService.class);


    // 사용자가 보는 모든 웹툰 요청 불러오는 로직
    public Map<String, Object> getWebtoon(int webtoonId) throws NoPermissionException {

        Webtoon webtoon = webtoonRepository.findByUuid(webtoonId);

        Map<String, Object> result = new HashMap<>();
        result.put("state", HttpStatus.OK);
        result.put("message", "웹툰 상세 정보입니다.");

        if(webtoon == null){
            result.put("data", null);
            return result;
        }

        WebtoonInfoDto webtoonInfo = WebtoonInfoDto.of(webtoon);
        try {

            if(webtoon.getFileId() != null){
                Integer fileId = webtoon.getFileId();
                logger.info("어드민 웹툰 요청에 대해 fileID 확인 : " + fileId);

                try {
                    FileInfoRegister fileOfRequest = fileRepository.findByFileId(fileId);
                    FileInfoResponseDto fileInfoDto = FileInfoResponseDto.of(fileOfRequest);
                    webtoonInfo.setFileInfo(fileInfoDto);
                } catch (Exception e) {
                    webtoonInfo.setFileInfo(null);
                }
            }

            WebtoonFileInfo completeFiles = webtoonFileRepository.findByWebtoonId(webtoonId); // nullable 하지..

            if(completeFiles != null) {
                CompleteFilesInfoDto webtoonFiles = CompleteFilesInfoDto.toDto(completeFiles);
                webtoonInfo.setCompleteFiles(webtoonFiles);
            }
            else{ // WebtoonFiles 자체가 null이면 안된다.
                webtoonInfo.setCompleteFiles(new CompleteFilesInfoDto());
            }

            result.put("data", webtoonInfo);

        }
        catch(Exception e){
            result.put("state", HttpStatus.BAD_REQUEST);
            result.put("message", e.getMessage());

        }

        return result;
    }


    // 사용자가 보는 모든 웹툰 요청 불러오는 로직
    public ReadResponseDto getAllWebtoons(int pageNo, int pageSize) throws NoPermissionException, SQLException {

        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "uuid"));

        Page<WebtoonInfoDto> webtoonInfoPages = webtoonRepository.findAll(pageRequest).map(WebtoonInfoDto::of);
        List<WebtoonInfoDto> webtoonInfos = webtoonInfoPages.getContent();

        ReadResponseDto results = new ReadResponseDto();

        int totalPages = webtoonInfoPages.getTotalPages();
        long totalElements = webtoonInfoPages.getTotalElements();
        int maximumElementsPerPage = 10;

        // 페이지 정보 기록
        results.setLast(pageNo == totalPages - 1);
        results.setMaximumElementsPerPage(maximumElementsPerPage);
        results.setTotalPages(totalPages);
        results.setTotalElements(totalElements);

        try {
            for (WebtoonInfoDto toon : webtoonInfos) {
                int userId = toon.getUserId();
                var curUser = userRepository.findById(userId).get();

                UserInfo userInfo = new UserInfo();
                userInfo.setEmail(curUser.getEmail());
                userInfo.setNickname(curUser.getNickname());
                //userInfo.setUrl;
                toon.setUser(userInfo);

                /*
                    webtoon
                    : progress_state가 정상적으로 안 나옴 (방금 생성했는데 complete)
                    : fileInfo null
                    : title 안 온 거
                 */


                if(toon.getFileId() != null){
                    Integer fileId = toon.getFileId();
                    logger.info("어드민 웹툰 요청에 대해 fileID 확인 : " + fileId);

                    try {
                        FileInfoRegister fileOfRequest = fileRepository.findByFileId(fileId);
                        FileInfoResponseDto fileInfoDto = FileInfoResponseDto.of(fileOfRequest);
                        toon.setFileInfo(fileInfoDto);
                    } catch (Exception e) {
                        toon.setFileInfo(null);
                    }
                }

                int webtoonId = toon.getUuid();
                WebtoonFileInfo files = webtoonFileRepository.findByWebtoonId(webtoonId); // nullable 하지..

                if(files != null) {
                    CompleteFilesInfoDto webtoonFiles = CompleteFilesInfoDto.toDto(files);
                    toon.setCompleteFiles(webtoonFiles);
                }
                else{ // WebtoonFiles 자체가 null이면 안된다.
                    toon.setCompleteFiles(new CompleteFilesInfoDto());
                }

                //results.add(wt);
            }

            results.setDatas(webtoonInfos);

            System.out.println("WEBTOON INFO SIZE : " + webtoonInfos.size());

            if (webtoonInfos.isEmpty()) {
                results.setState(HttpStatus.OK);
                results.setMessage("존재하는 웹툰 요청이 없습니다.");
                //System.out.println("웹툰 리스트가 비어있습니다! 정상정상");
            }
            else{
                results.setState(HttpStatus.OK);
                results.setMessage("웹툰 요청 리스트입니다.");
            }
        }
        catch(Exception e){
            System.out.println("ERROR : " + e.getMessage());
        }

        return results;
    }

    public ReadResponseDto getAllWebtoons(int pageNo, int pageSize, boolean unreadSorted, String filter, int userId) throws NoPermissionException {

        PageRequest pageRequest = null;
        Page<WebtoonInfoDto> webtoonInfoPages = null;

        /*

            filter과 unreadSorted가 둘 다 없을 때 -> 최신순
            unreadSorted와 filter가 둘 다 있을 때 ->
            filter만 있고 unreadSorted가 없을 때 -> filter만
            unreadSorted만 있고 filter가 없을 때 -> 안읽은 순 & 완료된 페이지를 위로, 나머지는 최신순

         */

        if(unreadSorted) {
            // FIX : 변경 조건 - sort는 요청 상태가 안 읽음이면서도 요쳥이 완료된 것들만 위에 뜨고, 나머지는 최신 순으로 해야한다.

            pageRequest = PageRequest.of(pageNo, pageSize);

            if(filter == null){
                webtoonInfoPages = webtoonRepository.findWebtoonWithPriorityWithoutFilter(userId, pageRequest).map(WebtoonInfoDto::of);
            }
            else{
                int progress = WebtoonProgressState.getProgress(filter);
                System.out.println("PROGRESS NUMBER : " + progress);
                webtoonInfoPages = webtoonRepository.findWebtoonWithPriorityWithFilter(userId, progress, pageRequest).map(WebtoonInfoDto::of);
            }

        }
        else {
            pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "uuid"));

            if(filter == null){
                webtoonInfoPages = webtoonRepository.findByUserId(userId, pageRequest).map(WebtoonInfoDto::of);
            }
            else {

                int progress = WebtoonProgressState.getProgress(filter);
                System.out.println("PROGRESS NUMBER : " + progress);
                webtoonInfoPages = webtoonRepository.findByUserIdAndProgress(userId, progress, pageRequest).map(WebtoonInfoDto::of);
            }
        }


        List<WebtoonInfoDto> webtoonInfos = webtoonInfoPages.getContent();

        ReadResponseDto results = new ReadResponseDto();

        int totalPages = webtoonInfoPages.getTotalPages();
        long totalElements = webtoonInfoPages.getTotalElements();
        int maximumElementsPerPage = 10;

        // 페이지 정보 기록
        results.setLast(pageNo == totalPages - 1);
        results.setMaximumElementsPerPage(maximumElementsPerPage);
        results.setTotalPages(totalPages);
        results.setTotalElements(totalElements);

        try {
            for (WebtoonInfoDto toon : webtoonInfos) {
                var curUser = userRepository.findById(userId).get();

                UserInfo userInfo = new UserInfo();
                userInfo.setEmail(curUser.getEmail());
                userInfo.setNickname(curUser.getNickname());
                //userInfo.setUrl;
                toon.setUser(userInfo);

                if(toon.getFileId() != null){
                    Integer fileId = toon.getFileId();

                    logger.info("유저 웹툰 요청에 대해 fileID 확인 : " + fileId);
                    try {
                        FileInfoRegister fileOfRequest = fileRepository.findByFileId(fileId);
                        System.out.println("URL : " + fileOfRequest.getUrl());
                        FileInfoResponseDto fileInfoDto = FileInfoResponseDto.of(fileOfRequest);
                        toon.setFileInfo(fileInfoDto);
                    } catch (Exception e) {
                        logger.error("FILE INFO 저장 에러입니다. :" + e.getMessage());
                    }
                }

                int webtoonId = toon.getUuid();
                WebtoonFileInfo files = webtoonFileRepository.findByWebtoonId(webtoonId); // nullable 하지..

                if(files != null) {
                    CompleteFilesInfoDto webtoonFiles = CompleteFilesInfoDto.toDto(files);
                    toon.setCompleteFiles(webtoonFiles);
                }
                else{ // WebtoonFiles 자체가 null이면 안된다.
                    toon.setCompleteFiles(new CompleteFilesInfoDto());
                }
            }

            results.setDatas(webtoonInfos);

            System.out.println("WEBTOON INFO SIZE : " + webtoonInfos.size());

            if (webtoonInfos.isEmpty()) {
                results.setState(HttpStatus.OK);
                results.setMessage("존재하는 웹툰 요청이 없습니다.");
                //System.out.println("웹툰 리스트가 비어있습니다! 정상정상");
            }
            else{
                results.setState(HttpStatus.OK);
                results.setMessage("고객들의 웹툰 요청 리스트입니다.");
            }
        }
        catch(Exception e){
            System.out.println("ERROR : " + e.getMessage());
        }

        return results;
    }


    @Transactional
    // 웹툰 생성 로직
    public Map<String, Object> createWebtoon(String email, WebtoonCreateRequestDto webtoonInfo) throws NoPermissionException, SQLException {

        try {
            var user = userRepository.findByEmail(email).get();

            Map<String, Object> map = new HashMap<>();

            Webtoon webtoon = Webtoon
                    .builder()
                    .title(webtoonInfo.getTitle())
                    .content(webtoonInfo.getContent())
                    .style(webtoonInfo.getStyle())
                    .tags(webtoonInfo.getTags())
                    .fileId(webtoonInfo.getFileId())
                    .count(webtoonInfo.getCount())
                    .progress(WebtoonProgressState.CONTINUE.getProgress())
                    .build();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
            String now_dt = format.format(now);

            webtoon.setCreatedAt(now_dt);
            webtoon.setUserId(user.getUuid());
            //webtoon.setFileId(webtoonCount);

            webtoonRepository.save(webtoon);

            map.put("state", HttpStatus.CREATED);
            map.put("message", "웹툰이 정상적으로 생성되었습니다.");

            return map;
        }
        catch (IllegalArgumentException e){
            System.out.println("MSG : " + e.getMessage());
            if(e.getMessage().contains("fileId"))
                throw new IllegalArgumentException("잘못된 id 값입니다.");
            else
                throw new IllegalArgumentException(e.getMessage());
        }
        catch (Exception e){
            System.out.println("EXCEPTION MSG : " + e.getMessage());
            if(e.getMessage().contains("fileId"))
                throw new IllegalArgumentException("잘못된 id 값입니다.");
            else
                throw new IllegalArgumentException(e.getMessage());
        }

    }

    // 어드민이 웹툰 요청 완료 여부 상태를 변경하는 로직
    @Transactional
    public void changeWebtoonRequestFlag(int uuid, boolean flag){
        var webtoon = webtoonRepository.findById(uuid)
                .orElseThrow(()-> {throw new IllegalStateException(ErrorCode.INTERNAL_ERROR.getMessage());});
        webtoon.setSuccessFlag(flag);
    }

    @Transactional
    public void patchWebtoon(int webtoonId){
        var webtoon = webtoonRepository.findById(webtoonId)
                .orElseThrow(()-> {throw new IllegalStateException(ErrorCode.INTERNAL_ERROR.getMessage());});

        webtoon.setRead(true);
    }

    public WebtoonStateResponseDto getWebtoonState(int userId){

         List<Webtoon> webtoons = webtoonRepository.findByUserId(userId);

         int rejected = 0;
         int continued = 0;
         int completed = 0;
         int total = 0;

         for(Webtoon webtoon : webtoons){

             String state = WebtoonProgressState.getState(webtoon.getProgress());

             if(state.equals("REJECT")){
                 rejected++;
             }

             if(state.equals("CONTINUE")){
                 continued++;
             }

             if(state.equals("COMPLETE")){
                 completed++;
             }
         }

         total = webtoons.size();

         WebtoonStateResponseDto response = new WebtoonStateResponseDto();
         response.setState(HttpStatus.OK);
         response.setMessage("현재까지 진행 중인 작품 수입니다.");

         response.setCompleted(completed);
         response.setContinued(continued);
         response.setRejected(rejected);
         response.setTotal(total);

         return response;
    }

    @Transactional
    public Map<String, Object> changeProgressState(int webtoonId, String progressState) throws Exception {

        try {

            System.out.println("PROGRESS NAME : " + progressState);
            Map<String, Object> result = new HashMap<>();
            int progress = WebtoonProgressState.getProgress(progressState);

            if(progress == -1){
                result.put("state", HttpStatus.BAD_REQUEST);
                result.put("message", "변경하고 싶은 상태를 정확히 입력해주세요.");
                return result;
            }

            Webtoon webtoon = webtoonRepository.findByUuid(webtoonId);
            webtoon.setProgress(progress);

            result.put("state", HttpStatus.OK);
            result.put("message", "변경되었습니다.");

            return result;
        }
        catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

}
