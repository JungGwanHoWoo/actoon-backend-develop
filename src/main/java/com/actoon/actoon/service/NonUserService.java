package com.actoon.actoon.service;

import com.actoon.actoon.dto.NonUserDto.*;
import com.actoon.actoon.repository.NonUserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class NonUserService {

    NonUserRepository nonUserRepository;

    public NonUserService(NonUserRepository nonUserRepository){
        this.nonUserRepository = nonUserRepository;
    }

    // 비회원 유저 정보 저장하는 로직
    public void register(NonUserRegisterRequestDto nonuser){

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        String now_dt = format.format(now);

        nonuser.setCreated_at(now_dt);

        nonUserRepository.save(nonuser.toEntity());
    }

    // 비회원 리스트를 가져오는 로직
    public NonUserListResponseDto getList(int pageNo, int pageSize){

        // sort 기준을 열거형으로 생성
        // 정렬 순서 및 properties를 프론트에서 넣을 수 있도록 해야한다. 필터링 시 매번 백엔드에서 고칠 수 없기 때문이다!
        // pagination 시 필요한 모든 파라미터는 프론트에서 정하도록 해야한다.

        // 페이지네이션은 정렬 기준이 굳이 필요가 없다.
        // 정렬은 굳이 필요가 없구만.. 알겠습니다!
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "uuid"));

        Page<NonUserInfoDto> pages = nonUserRepository.findAll(pageRequest).map(NonUserInfoDto::of);
        List<NonUserInfoDto> nonUserLists = pages.getContent();

        int totalPages = pages.getTotalPages();
        long totalElements = pages.getTotalElements();
        boolean last = totalPages == pageNo;
        //List<NonUserResponseDto> nonUserDtos = new ArrayList<>();
        NonUserListResponseDto response = new NonUserListResponseDto(HttpStatus.OK, "구독 신청한 비회원 리스트입니다.", nonUserLists, totalPages, totalElements, 10, last);

        return response;
    }
}
