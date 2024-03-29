package com.encore.thecatch.Post.service;

import com.encore.thecatch.Post.Entity.Image;
import com.encore.thecatch.Post.Entity.Post;
import com.encore.thecatch.Post.dto.Request.CreatePostReq;
import com.encore.thecatch.Post.dto.Request.UpdatePostReq;
import com.encore.thecatch.Post.repository.ImageRepository;
import com.encore.thecatch.Post.repository.PostRepository;
import com.encore.thecatch.common.S3.S3Service;
import com.encore.thecatch.user.domain.User;
import com.encore.thecatch.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final S3Service s3Service;

    @PreAuthorize("hasAuthority('USER')")
    public Post createPost(CreatePostReq createPostReq) {
        List<String> imgPaths = null;

//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Member findMember = memberRepository.findByEmail(authentication.getName())
//                .orElseThrow(MemberNotFoundException::new);
        Long id = 1L;
        User findUser = userRepository.findById(id).orElseThrow(EntityNotFoundException::new);


        if (createPostReq.getImages() != null) {
            imgPaths = s3Service.upload("Post", createPostReq.getImages());
        }

        Post newPost = createPostReq.toEntity(imgPaths, findUser);
        postRepository.save(newPost);

        if (imgPaths != null) {
            List<String> imgList = new ArrayList<>();

            for (String imgUrl : imgPaths) {
                Image img = new Image(imgUrl, newPost);
                imageRepository.save(img);
                imgList.add(img.getImgUrl());
            }
        }

        return newPost;
    }

    public Post deletePost(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
        post.isDelete();
        return post;
    }

    public Post detailPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));
    }

    public Post updatePost(Long id, UpdatePostReq updatePostReq) {
        Post post = postRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

        post.updatePost(updatePostReq.getTitle(), updatePostReq.getCategory(), updatePostReq.getContents());

        return post;
    }


    public Page<Post> myPostList(Long id, Pageable pageable) {
        Long userId = 1L;
        return postRepository.findAllByUserId(userId, pageable);
    }
}
