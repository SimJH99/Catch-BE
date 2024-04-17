package com.encore.thecatch.complaint.controller;

import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.complaint.dto.request.AddImageReq;
import com.encore.thecatch.complaint.dto.request.CreateComplaintReq;
import com.encore.thecatch.complaint.dto.request.SearchComplaintCondition;
import com.encore.thecatch.complaint.dto.request.UpdateComplaintReq;
import com.encore.thecatch.complaint.dto.response.*;
import com.encore.thecatch.complaint.entity.Complaint;
import com.encore.thecatch.complaint.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintService complaintService;

    //게시판 생성
    @PostMapping("/create")
    public ResponseDto createComplaint(CreateComplaintReq createComplaintReq) {
        Complaint complaint = complaintService.createComplaint(createComplaintReq);
        return new ResponseDto(HttpStatus.CREATED,
                ResponseCode.SUCCESS_CREATE_POST,
                new DefaultResponse<Long>(complaint.getId()));
    }

    // 게시글 상세 정보 보기
    @GetMapping("/{id}/detail")
    public ResponseDto detailComplaint(@PathVariable Long id) {
        return new ResponseDto(HttpStatus.OK,
                ResponseCode.SUCCESS_FOUND_POST,
                new DefaultResponse<DetailComplaintRes>(complaintService.detailComplaint(id)));
    }


    //내 글 보기
    @GetMapping("/myPosts")
    public ResponseDto myComplaintList(Pageable pageable) {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_FOUND_MY_POSTS,
                new DefaultResponse<Page<MyComplaintRes>>(complaintService.myComplaintList(pageable)));
    }

    // 게시글 리스트 보기 (검색 로직)
    @PostMapping("/list")
    public ResponseDto searchComplaint(@RequestBody SearchComplaintCondition searchComplaintCondition, Pageable pageable) throws Exception {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_POST_LIST,
                new DefaultResponse<Page<ListComplaintRes>>(complaintService.searchComplaint(searchComplaintCondition, pageable)));
    }

    //게시글 글 내용, 제목 수정
    @PatchMapping("/{id}/update")
    public ResponseDto updateComplaint(@PathVariable Long id, UpdateComplaintReq updateComplaintReq) {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_UPDATE_MY_POST,
                new DefaultResponse<UpdateComplaintRes>(complaintService.updateComplaint(id, updateComplaintReq)));
    }

    //사진 추가
    @PostMapping("/{id}/add/image")
    public ResponseDto addImage(@PathVariable Long id, AddImageReq addImageReq){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_ADD_IMAGES,
                new DefaultResponse<AddImageRes>(complaintService.addImage(id, addImageReq)));
    }

    //사진 삭제
    @DeleteMapping("/{id}/delete/image")
    public ResponseDto deleteImage(@PathVariable Long id) {
        complaintService.deleteImage(id);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_DELETE_IMAGES,
                new DefaultResponse<String>("delete Image"));
    }

    //게시글 삭제
    @DeleteMapping("/{id}/delete")
    public ResponseDto deleteComplaint(@PathVariable Long id) {
        Complaint complaint = complaintService.deletePost(id);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_DELETE_MY_POST, new DefaultResponse<Long>(complaint.getId()));
    }

    @GetMapping("/countAll")
    public ResponseDto countAllComplaint(){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS,
                new DefaultResponse<Long>(complaintService.countAllComplaint()));
    }

    @GetMapping("/countStatus")
    public ResponseDto countStatusComplaint(){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS,
                new DefaultResponse<List<CountStatusComplaintRes>>(complaintService.countStatusComplaint()));
    }
}
