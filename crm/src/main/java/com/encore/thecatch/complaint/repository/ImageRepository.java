package com.encore.thecatch.complaint.repository;

import com.encore.thecatch.complaint.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findAllByComplaintId(Long postId);
}
