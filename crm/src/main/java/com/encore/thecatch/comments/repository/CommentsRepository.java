package com.encore.thecatch.comments.repository;

import com.encore.thecatch.comments.entity.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CommentsRepository extends JpaRepository<Comments, Long> {
    Optional<Comments> findByComplaintIdAndActive(Long id, boolean active);
}
