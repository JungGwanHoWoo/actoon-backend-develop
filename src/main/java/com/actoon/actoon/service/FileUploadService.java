package com.actoon.actoon.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.actoon.actoon.domain.FileInfoRegister;
import com.actoon.actoon.domain.NoticeBoardFileInfo;
import com.actoon.actoon.domain.WebtoonFileInfo;
import com.actoon.actoon.dto.UploadFileDto.UploadCompleteFileRequestDto;
import com.actoon.actoon.dto.UploadFileDto.UploadFileRequestDto;
import com.actoon.actoon.repository.ChainRepository;
import com.actoon.actoon.repository.FileUploadRepository;
import com.actoon.actoon.repository.NoticeBoardFileRepository;
import com.actoon.actoon.repository.UserRepository;
import com.actoon.actoon.repository.WebtoonFileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileUploadService {
    //private final S3Uploader s3Uploader;

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Autowired
    private ChainRepository chainRepository;

    @Autowired
    private WebtoonFileRepository webtoonFileRepository;

    @Autowired
    private NoticeBoardFileRepository noticeBoardFileRepository;

    @Autowired
    private UserRepository usersRepository;

    @Value("${upload.path}")
    private String uploadPath;

    // 파일을 저장하는 로직을 수행하는 메서드
    @Transactional
    public Map<String, Object> storeWebtoonFile(UploadFileRequestDto file) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        String url = "";

        Map<String, Object> map = new HashMap<>();

        if (file.getFile() == null || file.getFile().getSize() <= 0 || file.getFile().isEmpty()) {
            map.put("state", HttpStatus.BAD_REQUEST);
            map.put("message", "파일을 첨부해주세요.");
            return map;
        }
        MultipartFile uploadFile = file.getFile();

        String originalFilename = uploadFile.getOriginalFilename();
        String saveFileName = createSaveFileName(originalFilename);
        // 2-1.서버에 파일 저장 (로컬 저장소)
        uploadFile.transferTo(new File(getFullPath(saveFileName)));

        // 2-2. DB에 정보 저장
        String contentType = uploadFile.getContentType();

        System.out.println("PATH : " + getFullPath(saveFileName));

        var user = usersRepository.findByEmail(email).get();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String now_dt = format.format(now);

        FileInfoRegister fileInfoRegister = FileInfoRegister.builder()
                .url(saveFileName)
                .created_at(now_dt)
                .userId(user.getUuid())
                //.contentType(contentType)
                //.deleteFlag(false).build();
                .build();

        fileUploadRepository.saveAndFlush(fileInfoRegister);

        int fileId = fileInfoRegister.getFileId();

        map.put("fileId", fileId);
        map.put("url", saveFileName);

        return map;

//        catch(Exception e){
//            Map<String, Object> map = new HashMap<>();
//            map.put("errors", e.getMessage());
//            return map;
//        }
    }

    @Transactional
    public Map<String, Object> storeNoticeBoardFile(MultipartFile file) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Map<String, Object> map = new HashMap<>();

        if (file == null || file.getSize() <= 0 || file.isEmpty()) {
            map.put("state", HttpStatus.BAD_REQUEST);
            map.put("message", "파일을 첨부해주세요.");
            return map;
        }

        String originalFilename = file.getOriginalFilename();
        String saveFileName = createSaveFileName(originalFilename);

        // 파일 저장
        file.transferTo(new File(getFullPath(saveFileName)));

        var user = usersRepository.findByEmail(email).get();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String now_dt = format.format(now);

        FileInfoRegister fileInfoRegister = FileInfoRegister.builder()
                .url(saveFileName)
                .created_at(now_dt)
                .userId(user.getUuid())
                .build();

        fileUploadRepository.saveAndFlush(fileInfoRegister);

        int fileId = fileInfoRegister.getFileId();

        map.put("fileId", fileId);
        map.put("url", saveFileName);

        return map;
    }

    @Transactional
    public Map<String, Object> storeUserProfileFile(UploadFileRequestDto file, int userId) throws IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName();
        String url = "";

        Map<String, Object> map = new HashMap<>();

        var user = usersRepository.findById(userId).get();

        if (file.getFile() == null || file.getFile().getSize() <= 0 || file.getFile().isEmpty()) {
            int fileId = user.getFileId();
            FileInfoRegister fileReg = fileUploadRepository.findByFileId(fileId);

            fileUploadRepository.delete(fileReg);

            user.setFileId(null);
            user.setProfile(null);
            map.put("state", HttpStatus.OK);
            map.put("message", "정상적으로 요청되었습니다.");
            return map;
        }

        MultipartFile uploadFile = file.getFile();

        String originalFilename = uploadFile.getOriginalFilename();
        String saveFileName = createSaveFileName(originalFilename);
        // 2-1.서버에 파일 저장 (로컬 저장소)
        uploadFile.transferTo(new File(getFullPath(saveFileName)));

        // 2-2. DB에 정보 저장
        String contentType = uploadFile.getContentType();

        System.out.println("PATH : " + getFullPath(saveFileName));

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String now_dt = format.format(now);

        FileInfoRegister fileInfoRegister = FileInfoRegister.builder()
                .url(saveFileName)
                .created_at(now_dt)
                .userId(userId)
                //.contentType(contentType)
                //.deleteFlag(false).build();
                .build();

        fileUploadRepository.saveAndFlush(fileInfoRegister);

        int fileId = fileInfoRegister.getFileId();

        user.setFileId(fileId);
        user.setProfile(saveFileName);

        map.put("fileId", fileId);
        map.put("url", saveFileName);

        return map;

//        catch(Exception e){
//            Map<String, Object> map = new HashMap<>();
//            map.put("errors", e.getMessage());
//            return map;
//        }
    }

    // CHECK : 기능 되는지 확인
    @Transactional
    public Map<String, Object> storeCompleteFile(int webtoonId, UploadCompleteFileRequestDto uploadFile) throws IOException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Map<String, Object> map = new HashMap<>();
            map.put("webtoonId", webtoonId);

            if (uploadFile.getFile() == null || uploadFile.getFile().isEmpty() || uploadFile.getFile().getSize() <= 0) {
                map.put("state", HttpStatus.BAD_REQUEST);
                map.put("message", "파일을 첨부해주세요.");

                return map;
            }

            MultipartFile file = uploadFile.getFile();
            String type = uploadFile.getType();

            String originalFilename = file.getOriginalFilename(); // null pointer 위험

            String saveFileName = createSaveFileName(originalFilename);

            // 2-1.서버에 파일 저장 (로컬 저장소) -> 아무것도 없어도 저장되게 해놨네
            file.transferTo(new File(getFullPath(saveFileName)));

            // 2-2. DB에 정보 저장
            String contentType = file.getContentType();

            //System.out.println("PATH : " + getFullPath(saveFileName));
            WebtoonFileInfo webtoonFileInfo = webtoonFileRepository.findByWebtoonId(webtoonId);

            if (webtoonFileInfo == null) {
                // 생성 후 데이터를 삽입

                webtoonFileInfo = WebtoonFileInfo
                        .builder()
                        .webtoonId(webtoonId)
                        .build();
            }

            if (type.equalsIgnoreCase("ZIP")) {
                webtoonFileInfo.setZip_url(saveFileName);
                map.put("zip_url", saveFileName);
            }

            if (type.equalsIgnoreCase("PDF")) {
                webtoonFileInfo.setPdf_url(saveFileName);
                map.put("pdf_url", saveFileName);
            }

            if (type.equalsIgnoreCase("IMG")) {
                webtoonFileInfo.setImg_url(saveFileName);
                map.put("img_url", saveFileName);
            }

            webtoonFileRepository.save(webtoonFileInfo);

            map.put("state", HttpStatus.CREATED);
            map.put("message", "웹툰 완성 파일이 정상적으로 업로드 되었습니다.");

            return map;
        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("state", HttpStatus.BAD_REQUEST);
            map.put("errors", e.getMessage());
            return map;
        }
    }

    @Transactional
    public Map<String, Object> storeCompleteNoticeBoardFile(int noticeBoardId, UploadCompleteFileRequestDto uploadFile) throws IOException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Map<String, Object> map = new HashMap<>();
            map.put("noticeBoardId", noticeBoardId);

            // 파일이 없거나 사이즈가 0일 경우 처리
            if (uploadFile.getFile() == null || uploadFile.getFile().isEmpty() || uploadFile.getFile().getSize() <= 0) {
                map.put("state", HttpStatus.BAD_REQUEST);
                map.put("message", "파일을 첨부해주세요.");
                return map;
            }

            MultipartFile file = uploadFile.getFile();
            String type = uploadFile.getType();

            // 파일 이름 생성 및 저장 경로 설정
            String originalFilename = file.getOriginalFilename();
            String saveFileName = createSaveFileName(originalFilename);

            // 파일을 로컬 저장소에 저장
            file.transferTo(new File(getFullPath(saveFileName)));

            // NoticeBoard 파일 정보 확인
            NoticeBoardFileInfo noticeBoardFileInfo = noticeBoardFileRepository.findByNoticeBoardId(noticeBoardId);

            if (noticeBoardFileInfo == null) {
                // 새로운 파일 정보를 생성
                noticeBoardFileInfo = NoticeBoardFileInfo
                        .builder()
                        .noticeBoardId(noticeBoardId)
                        .build();
            }

            // 이미지 파일만 저장
            if (type.equalsIgnoreCase("IMG")) {
                noticeBoardFileInfo.setImg_url(saveFileName);
                map.put("img_url", saveFileName);
            } else {
                map.put("state", HttpStatus.BAD_REQUEST);
                map.put("message", "이미지 파일만 업로드 가능합니다.");
                return map;
            }

            // 파일 정보를 DB에 저장
            noticeBoardFileRepository.save(noticeBoardFileInfo);

            map.put("state", HttpStatus.CREATED);
            map.put("message", "게시판 이미지 파일이 정상적으로 업로드되었습니다.");
            return map;

        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("state", HttpStatus.BAD_REQUEST);
            map.put("errors", e.getMessage());
            return map;
        }
    }


    /*
    public FileDto.CompleteFilesInfoDto getFilesUrl(int webtoonId, UploadCompleteFileRequestDto uploadFile){

        WebtoonFileInfo fileInfo = webtoonFileRepository.findByWebtoonId(webtoonId);

        FileDto.CompleteFilesInfoDto completeFile = FileDto.CompleteFilesInfoDto.toDto(fileInfo);

        return completeFile;
    }*/
//    private String getFile(MultipartFile uploadFile) throws IOException {
//        String originalFilename = uploadFile.getOriginalFilename(); // null pointer 위험
//        String saveFileName = createSaveFileName(originalFilename);
//
//        // 2-1.서버에 파일 저장 (로컬 저장소) -> 아무것도 없어도 저장되게 해놨네
//        uploadFile.transferTo(new File(getFullPath(saveFileName)));
//
//        // 2-2. DB에 정보 저장
//        String contentType = uploadFile.getContentType();
//
//        System.out.println("PATH : " + getFullPath(saveFileName));
//
//        return getFullPath(saveFileName);
//    }
    // 실제로 저장될 때 사용될 파일 이름 만드는 메서드
    private String createSaveFileName(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 확장자 명 추출하는 메서드
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

    // 실제 저장되는 파일 path를 반환하는 메서드
    private String getFullPath(String filename) {
        System.out.println("UPLOAD PATH : " + uploadPath);
        return uploadPath + filename;
    }

    private String getDownloadUrl(String filename) {
        return filename;
    }

    // 파일 절대 경로를 얻어오는 로직
    public Path load(String filename) {
        return Paths.get(uploadPath).resolve(filename);
    }

    // file을 Resource 객체로 만들어서 다운로드에 이용할 수 있도록 변환하는 로직
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read file: " + filename);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Could not read file: " + filename, e);
        }
    }

}
