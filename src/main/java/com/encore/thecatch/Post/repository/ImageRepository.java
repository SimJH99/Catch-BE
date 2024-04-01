package com.encore.thecatch.Post.repository;

import com.encore.thecatch.Post.Entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long>{

    List<Image> findAllByPostId(Long postId);
}
