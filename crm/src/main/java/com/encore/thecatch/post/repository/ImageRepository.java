package com.encore.thecatch.post.repository;

import com.encore.thecatch.post.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long>{

    List<Image> findAllByPostId(Long postId);
}
