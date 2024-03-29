package com.encore.thecatch.Post.controller;

import com.encore.thecatch.Post.Entity.Post;
import com.encore.thecatch.Post.dto.Request.CreatePostReq;
import com.encore.thecatch.Post.dto.Request.UpdatePostReq;
import com.encore.thecatch.Post.service.PostService;
import com.encore.thecatch.common.DefaultResponse;
import com.encore.thecatch.common.ResponseCode;
import com.encore.thecatch.common.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
        Post post = postService.detailPost(id);
        return new ResponseDto(HttpStatus.OK,
                ResponseCode.SUCCESS_FOUND_POST,
                new DefaultResponse<Long>(post.getId()));
    }


    //내 글 보기
    @GetMapping("/{id}/myPosts")
    public ResponseDto myPostList(@PathVariable Long id,
                                  @PageableDefault(size = 10, direction = Sort.Direction.DESC) Pageable pageable) {
        Page<Post> posts = postService.myPostList(id, pageable);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_FOUND_MY_POSTS, new DefaultResponse<Page<Post>>(posts));
    }

    //모든 글 보기
//    @GetMapping("/list")
//    public Page<ListPostRes> listPostRes(@PageableDefault(size = 10,  direction = Sort.Direction.DESC) Pageable pageable) {
//        return postService.listPostRes(pageable);
//    }

    //게시글 글 내용, 제목 수정
    @PatchMapping("/{id}/update")
    public ResponseDto updatePost(@PathVariable Long id, UpdatePostReq updatePostReq) {
        Post post = postService.updatePost(id, updatePostReq);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_UPDATE_MY_POST, new DefaultResponse<Long>(post.getId()));
    }

    //사진 추가

    //사진 수정

    //사진 삭제
    @DeleteMapping("/{id}/delete/image")
    public void deleteImage(@PathVariable Long id) {

    }

    //게시글 삭제
    @DeleteMapping("/{id}/delete")
    public ResponseDto deletePost(@PathVariable Long id) {
        Post post = postService.deletePost(id);
        return new ResponseDto(HttpStatus.OK, ResponseCode.SUCCESS_DELETE_MY_POST, new DefaultResponse<Long>(post.getId()));
    }
}
