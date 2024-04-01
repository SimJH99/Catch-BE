package com.encore.thecatch.post.repository;

import com.encore.thecatch.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByUserIdAndActive(Long userId, Pageable pageable, int active);
}
