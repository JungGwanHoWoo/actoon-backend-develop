package com.actoon.actoon.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NoPermissionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.actoon.actoon.domain.Chain;
import com.actoon.actoon.domain.FileInfoRegister;
import com.actoon.actoon.domain.NoticeBoard;
import com.actoon.actoon.domain.NoticeBoardFileInfo;
import com.actoon.actoon.dto.FileDto.CompleteFilesInfoDto;
import com.actoon.actoon.dto.FileDto.CompleteNoticeBoardFilesInfoDto;
import com.actoon.actoon.dto.FileInfoDto.FileInfoResponseDto;
import com.actoon.actoon.dto.NoticeBoardInfoDTO;
import com.actoon.actoon.dto.NoticeBoardRequestDTO.NoticeBoardCreateRequestDTO;
import com.actoon.actoon.dto.NoticeBoardRequestDTO.NoticeBoardUpdateRequestDTO;
import com.actoon.actoon.dto.NoticeBoardResponseDTO.NoticeBoardStateResponseDto;
import com.actoon.actoon.dto.NoticeBoardResponseDTO.ReadResponseDto;
import com.actoon.actoon.dto.UserDto.UserInfo;
import com.actoon.actoon.repository.ChainRepository;
import com.actoon.actoon.repository.FileUploadRepository;
import com.actoon.actoon.repository.NoticeBoardFileRepository;
import com.actoon.actoon.repository.NoticeBoardRepository;
import com.actoon.actoon.repository.UserRepository;

// 웹툰에 관련한 요청을 처리하는 서비스 로직
@Service
public class NoticeBoardService {

    @Autowired
    NoticeBoardRepository noticeBoardRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    private ChainRepository chainRepository;

    @Autowired
    FileUploadRepository fileRepository;

    @Autowired
    NoticeBoardFileRepository noticeBoardFileRepository;

    private static final Logger logger = LoggerFactory.getLogger(NoticeBoardService.class);

    // 사용자가 보는 모든 게시판 요청 불러오는 로직
    public Map<String, Object> getNoticeBoard(int noticeBoardId) throws NoPermissionException {
        NoticeBoard noticeBoard = noticeBoardRepository.findByUuid(noticeBoardId);

        Map<String, Object> result = new HashMap<>();
        result.put("state", HttpStatus.OK);
        result.put("message", "게시판 상세 정보입니다.");

        if (noticeBoard == null) {
            result.put("data", null);
            return result;
        }

        NoticeBoardInfoDTO noticeBoardInfo = NoticeBoardInfoDTO.of(noticeBoard);
        try {
            // 1. noticeBoardId로 관련된 모든 Chain 객체들을 가져옵니다
            List<Chain> chains = chainRepository.findByBoardId(noticeBoardId);

            // 2. 파일 정보를 저장할 리스트를 생성합니다
            List<FileInfoResponseDto> fileInfoList = new ArrayList<>();

            // 3. 각 Chain의 fileId로 파일 정보를 조회합니다
            for (Chain chain : chains) {
                int fileId = chain.getFileId();
                try {
                    FileInfoRegister fileInfo = fileRepository.findByFileId(fileId);
                    if (fileInfo != null && !fileInfo.isDeleteFlag()) {  // deleteFlag가 false인 파일만 포함
                        FileInfoResponseDto fileInfoDto = FileInfoResponseDto.of(fileInfo, chain);
                        fileInfoList.add(fileInfoDto);
                    }
                } catch (Exception e) {
                    logger.error("파일 정보 조회 실패 - fileId: " + fileId, e);
                }
            }

            // 5. 수집된 파일 정보 리스트를 DTO에 설정합니다
            noticeBoardInfo.setFileInfoList(fileInfoList);

            // 기존 CompleteFiles 처리 로직 유지
            NoticeBoardFileInfo completeFiles = noticeBoardFileRepository.findByNoticeBoardId(noticeBoardId);
            if (completeFiles != null) {
                CompleteFilesInfoDto noticeBoardFiles = CompleteNoticeBoardFilesInfoDto.toDto(completeFiles);
                noticeBoardInfo.setCompleteFiles(noticeBoardFiles);
            } else {
                noticeBoardInfo.setCompleteFiles(new CompleteNoticeBoardFilesInfoDto());
            }

            result.put("data", noticeBoardInfo);

        } catch (Exception e) {
            logger.error("게시판 정보 조회 실패", e);
            result.put("state", HttpStatus.BAD_REQUEST);
            result.put("message", e.getMessage());
        }

        return result;
    }

    // 사용자가 보는 모든 게시판 요청 불러오는 로직
    public ReadResponseDto getAllNoticeBoards(int pageNo, int pageSize) throws NoPermissionException, SQLException {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "uuid"));

        // userId와 상관없이 모든 게시판 조회
        Page<NoticeBoardInfoDTO> noticeBoardInfoPages = noticeBoardRepository.findAll(pageRequest).map(NoticeBoardInfoDTO::of);
        List<NoticeBoardInfoDTO> noticeBoardInfos = noticeBoardInfoPages.getContent();

        ReadResponseDto results = createResponseDto(noticeBoardInfoPages, pageNo);
        noticeBoardInfos.forEach(this::populateNoticeBoardInfo); // 공지사항 정보 설정

        results.setDatas(noticeBoardInfos);
        return results;
    }

    public Map<String, Object> createNoticeBoard(String email, NoticeBoardCreateRequestDTO noticeBoardInfo) throws NoPermissionException, SQLException {
        try {
            var user = userRepository.findByEmail(email).get();
            Map<String, Object> map = new HashMap<>();

            // 게시판 엔티티 생성
            NoticeBoard noticeBoard = NoticeBoard
                    .builder()
                    .title(noticeBoardInfo.getTitle())
                    .content(noticeBoardInfo.getContent())
                    .fileId(null) // fileId 기본값을 null로 설정
                    .build();

            // 현재 날짜 설정
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date now = new Date();
            String now_dt = format.format(now);

            noticeBoard.setCreatedAt(now_dt);
            noticeBoard.setUserId(user.getUuid());
            noticeBoardRepository.save(noticeBoard);

            // fileIds가 null이 아니고 비어있지 않은 경우에만 Chain 엔티티 처리
            if (noticeBoardInfo.getFileIds() != null && !noticeBoardInfo.getFileIds().isEmpty()) {
                // 첫 번째 fileId를 noticeBoard에 설정
                noticeBoard.setFileId(noticeBoardInfo.getFileIds().get(0));
                noticeBoardRepository.save(noticeBoard);

                // Chain 엔티티 생성 및 저장
                for (Integer fileId : noticeBoardInfo.getFileIds()) {
                    if (fileId != null) {  // fileId가 null이 아닌 경우에만 처리
                        Chain chain = Chain.builder()
                                .fileId(fileId)
                                .boardId(noticeBoard.getUuid())
                                .build();
                        chainRepository.save(chain);
                    }
                }
            }

            // 응답 데이터 설정
            map.put("state", HttpStatus.CREATED);
            map.put("message", "게시판이 정상적으로 생성되었습니다.");

            return map;

        } catch (IllegalArgumentException e) {
            System.out.println("MSG : " + e.getMessage());
            throw new IllegalArgumentException("잘못된 값입니다.");
        } catch (Exception e) {
            System.out.println("EXCEPTION MSG : " + e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Transactional
    public Map<String, Object> updateNoticeBoard(String email, Integer noticeBoardId, NoticeBoardUpdateRequestDTO noticeBoardInfo)
            throws NoPermissionException, SQLException, NoticeBoardNotFoundException {
        try {
            // 게시글 존재 여부 및 권한 확인
            NoticeBoard noticeBoard = noticeBoardRepository.findById(noticeBoardId)
                    .orElseThrow(() -> new NoticeBoardNotFoundException("해당 게시글을 찾을 수 없습니다."));

            // 게시글 정보 업데이트
            if (noticeBoardInfo.getTitle() != null) {
                noticeBoard.setTitle(noticeBoardInfo.getTitle());
            }
            if (noticeBoardInfo.getContent() != null) {
                noticeBoard.setContent(noticeBoardInfo.getContent());
            }

            // 파일 처리
            if (noticeBoardInfo.getFileIds() != null && !noticeBoardInfo.getFileIds().isEmpty()) {
                // 기존 Chain 데이터 삭제
                chainRepository.deleteByBoardId(noticeBoardId);

                // 새로운 Chain 데이터 생성
                for (Integer fileId : noticeBoardInfo.getFileIds()) {
                    if (fileId != null) {
                        // FileInfoRegister 존재 여부 확인
                        FileInfoRegister fileInfo = fileRepository.findById(fileId)
                                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 파일입니다: " + fileId));

                        Chain chain = Chain.builder()
                                .fileId(fileId)
                                .boardId(noticeBoardId)
                                .build();
                        chainRepository.save(chain);
                    }
                }

                // 대표 파일 ID 업데이트
                noticeBoard.setFileId(noticeBoardInfo.getFileIds().get(0));
            } else {
                // fileIds가 없을 때: Chain 데이터 삭제 및 대표 파일 ID 제거
                chainRepository.deleteByBoardId(noticeBoardId);
                noticeBoard.setFileId(null); // 대표 파일 ID 초기화
            }

            // 게시글 저장
            noticeBoardRepository.save(noticeBoard);

            // 응답 데이터 설정
            Map<String, Object> response = new HashMap<>();
            response.put("state", HttpStatus.OK);
            response.put("message", "게시글이 성공적으로 수정되었습니다.");
            response.put("noticeBoardId", noticeBoardId);

            return response;

        } catch (Exception e) {
            throw new SQLException("게시글 수정 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    // @Transactional
    // public Map<String, Object> updateNoticeBoard(String email, Integer noticeBoardId, NoticeBoardUpdateRequestDTO noticeBoardInfo)
    //         throws NoPermissionException, SQLException, NoticeBoardNotFoundException {
    //     try {
    //         // 사용자 정보 가져오기
    //         var user = userRepository.findByEmail(email).get();
    //         // 게시판 존재 여부 확인 - Integer를 사용하도록 수정
    //         NoticeBoard noticeBoard = noticeBoardRepository.findById(noticeBoardId)
    //                 .orElseThrow(() -> new NoticeBoardNotFoundException("해당 게시글을 찾을 수 없습니다."));
    //         Map<String, Object> map = new HashMap<>();
    //         // 게시판 정보 업데이트
    //         if (noticeBoardInfo.getTitle() != null) {
    //             noticeBoard.setTitle(noticeBoardInfo.getTitle());
    //         }
    //         if (noticeBoardInfo.getContent() != null) {
    //             noticeBoard.setContent(noticeBoardInfo.getContent());
    //         }
    //         // 게시판 저장
    //         noticeBoardRepository.save(noticeBoard);
    //         // 응답 데이터 설정
    //         map.put("state", HttpStatus.OK);
    //         map.put("message", "게시판이 정상적으로 수정되었습니다.");
    //         return map;
    //     } catch (IllegalArgumentException e) {
    //         System.out.println("MSG : " + e.getMessage());
    //         if (e.getMessage().contains("fileId")) {
    //             throw new IllegalArgumentException("잘못된 파일 ID 값입니다.");
    //         } else {
    //             throw new IllegalArgumentException(e.getMessage());
    //         }
    //     } catch (Exception e) {
    //         System.out.println("EXCEPTION MSG : " + e.getMessage());
    //         if (e.getMessage().contains("fileId")) {
    //             throw new IllegalArgumentException("잘못된 파일 ID 값입니다.");
    //         } else {
    //             throw new IllegalArgumentException(e.getMessage());
    //         }
    //     }
    // }
    @Transactional
    public Map<String, Object> deleteNoticeBoard(int noticeBoardId) throws SQLException {
        Map<String, Object> response = new HashMap<>();

        try {
            // 게시글을 찾음
            NoticeBoard noticeBoard = noticeBoardRepository.findById(noticeBoardId)
                    .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

            // 연관된 파일 삭제 (필요한 경우)
            if (noticeBoard.getFileId() != null) {
                Integer fileId = noticeBoard.getFileId();
                fileRepository.deleteById(fileId); // 파일 삭제
            }

            // 게시글 삭제
            noticeBoardRepository.deleteById(noticeBoardId);

            // 응답 메시지 설정
            response.put("state", HttpStatus.OK);
            response.put("message", "게시글이 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            response.put("state", HttpStatus.INTERNAL_SERVER_ERROR);
            response.put("message", "게시글 삭제 중 오류가 발생했습니다.");
            throw new SQLException("게시글 삭제 실패: " + e.getMessage());
        }

        return response;
    }

    private ReadResponseDto createResponseDto(Page<NoticeBoardInfoDTO> noticeBoardInfoPages, int pageNo) {
        ReadResponseDto results = new ReadResponseDto();
        results.setLast(pageNo == noticeBoardInfoPages.getTotalPages() - 1);
        results.setMaximumElementsPerPage(noticeBoardInfoPages.getSize());
        results.setTotalPages(noticeBoardInfoPages.getTotalPages());
        results.setTotalElements(noticeBoardInfoPages.getTotalElements());
        return results;
    }

    private void populateNoticeBoardInfo(NoticeBoardInfoDTO noticeBoard) {
        // 1. 작성자 정보 설정
        int userId = noticeBoard.getUserId();  // 공지사항 작성자의 ID를 가져옴
        var curUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        UserInfo userInfo = new UserInfo();
        userInfo.setEmail(curUser.getEmail());
        userInfo.setNickname(curUser.getNickname());
        noticeBoard.setUser(userInfo);  // 작성자 정보를 공지사항에 설정

        // 2. 파일 정보 설정
        if (noticeBoard.getFileId() != null) {
            try {
                FileInfoRegister fileOfRequest = fileRepository.findByFileId(noticeBoard.getFileId());
                FileInfoResponseDto fileInfoDto = FileInfoResponseDto.of(fileOfRequest);
                noticeBoard.setFileInfo(fileInfoDto);  // 파일 정보를 공지사항에 설정
            } catch (Exception e) {
                // 파일 정보가 없거나 오류가 발생한 경우 null 설정
                noticeBoard.setFileInfo(null);
            }
        } else {
            // 파일이 없는 경우 null로 설정
            noticeBoard.setFileInfo(null);
        }
    }

    public NoticeBoardStateResponseDto getNoticeBoardState(int userId) {
        // userId와 일치하는 게시글 리스트를 가져옴
        List<NoticeBoard> noticeBoards = noticeBoardRepository.findByUserId(userId);

        // 게시글 총 개수 계산
        int total = noticeBoards.size();

        // 응답 DTO 생성 및 설정
        NoticeBoardStateResponseDto response = new NoticeBoardStateResponseDto();
        response.setState(HttpStatus.OK);
        response.setMessage("현재까지 진행 중인 게시글 수입니다.");
        response.setTotal(total); // 게시글 총 개수 설정

        return response;
    }

    public class NoticeBoardNotFoundException extends RuntimeException {

        public NoticeBoardNotFoundException(String message) {
            super(message);
        }
    }

}
