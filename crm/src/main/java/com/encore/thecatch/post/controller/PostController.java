package com.encore.thecatch.post.controller;

import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import com.encore.thecatch.post.entity.Post;
import com.encore.thecatch.post.dto.request.AddImageReq;
import com.encore.thecatch.post.dto.request.CreatePostReq;
import com.encore.thecatch.post.dto.request.UpdatePostReq;
import com.encore.thecatch.post.dto.response.AddImageRes;
import com.encore.thecatch.post.dto.response.DetailPostRes;
import com.encore.thecatch.post.dto.response.UpdatePostRes;
import com.encore.thecatch.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //게시판 생성
    @PostMapping("/create")
    public ResponseDto createPost(CreatePostReq createPostReq) {
        Post post = postService.createPost(createPostReq);
        return new ResponseDto(HttpStatus.CREATED,
                ResponseCode.SUCCESS_CREATE_POST,
                new DefaultResponse<Long>(post.getId()));
    }

    // 게시글 상세 정보 보기
    @GetMapping("/{id}/detail")
    public ResponseDto detailPost(@PathVariable Long id) {
        return new ResponseDto(HttpStatus.OK,
                ResponseCode.SUCCESS_FOUND_POST,
                new DefaultResponse<DetailPostRes>(postService.detailPost(id)));
    }


    //내 글 보기
    @GetMapping("/{id}/myPosts/{page}")
    public ResponseDto myPostList(@PathVariable Long id, @PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_FOUND_MY_POSTS,
                new DefaultResponse<Page<Post>>(postService.myPostList(id, pageable)));
    }

    //모든 글 보기
//    @GetMapping("/list")
//    public Page<ListPostRes> listPostRes(@PageableDefault(size = 10,  direction = Sort.Direction.DESC) Pageable pageable) {
//        return postService.listPostRes(pageable);
//    }

    //게시글 글 내용, 제목 수정
    @PatchMapping("/{id}/update")
    public ResponseDto updatePost(@PathVariable Long id, UpdatePostReq updatePostReq) {
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_UPDATE_MY_POST,
                new DefaultResponse<UpdatePostRes>(postService.updatePost(id, updatePostReq)));
    }

    //사진 추가
    @PostMapping("/{id}/add/image")
    public ResponseDto addImage(@PathVariable Long id, AddImageReq addImageReq){
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_ADD_IMAGES,
                new DefaultResponse<AddImageRes>(postService.addImage(id, addImageReq)));
    }

    //사진 삭제
    @DeleteMapping("/{id}/delete/image")
    public ResponseDto deleteImage(@PathVariable Long id) {
        postService.deleteImage(id);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_DELETE_IMAGES,
                new DefaultResponse<String>("delete Image"));
    }

    //게시글 삭제
    @DeleteMapping("/{id}/delete")
    public ResponseDto deletePost(@PathVariable Long id) {
        Post post = postService.deletePost(id);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_DELETE_MY_POST, new DefaultResponse<Long>(post.getId()));
    }
}
