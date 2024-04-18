package com.encore.thecatch.comments.controller;

import com.encore.thecatch.comments.dto.request.CreateCommentsReq;
import com.encore.thecatch.comments.dto.request.UpdateCommentsReq;
import com.encore.thecatch.comments.dto.response.CreateCommentsRes;
import com.encore.thecatch.comments.dto.response.DetailCommentRes;
import com.encore.thecatch.comments.dto.response.UpdateCommentsRes;
import com.encore.thecatch.comments.dto.response.CreateCommentsRes;
import com.encore.thecatch.comments.entity.Comments;
import com.encore.thecatch.comments.service.CommentsService;
import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentsController {
    private final CommentsService commentsService;

    //답변 생성
    @PostMapping("/{id}/create")
    public ResponseDto createComment(@PathVariable Long id, @RequestBody CreateCommentsReq createCommentsReq) throws Exception {
        return new ResponseDto(HttpStatus.CREATED,
                ResponseCode.SUCCESS_CREATE_COMMENT,
                new DefaultResponse<CreateCommentsRes>(commentsService.createComment(id, createCommentsReq)));
    }

    //답변 조회
    @GetMapping("/{id}/detail")
    public ResponseDto detailComment(@PathVariable Long id) {
        return new ResponseDto(HttpStatus.OK,
                ResponseCode.SUCCESS_DETAIL_COMMENT,
                new DefaultResponse<DetailCommentRes>(commentsService.detailComment(id)));
    }

    //답변 수정
    @PatchMapping("/{id}/update")
    public ResponseDto updateComment(@PathVariable Long id, @RequestBody UpdateCommentsReq updateCommentsReq) {
        return new ResponseDto(HttpStatus.OK,
                ResponseCode.SUCCESS_UPDATE_COMMENT,
                new DefaultResponse<UpdateCommentsRes>(commentsService.updateComment(id, updateCommentsReq)));
    }

    //답변 삭제
    @DeleteMapping("/{id}/delete")
    public ResponseDto deleteComment(@PathVariable Long id) {
        return new ResponseDto(HttpStatus.OK,
                ResponseCode.SUCCESS_DELETE_COMMENT,
                new DefaultResponse<String>(commentsService.deleteComment(id)));
    }
}
