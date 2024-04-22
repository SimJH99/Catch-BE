package com.encore.thecatch.complaint.repository;

import com.encore.thecatch.complaint.entity.Complaint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    Page<Complaint> findAllByUserIdAndActive (Long id, Pageable pageable, boolean active);
}
