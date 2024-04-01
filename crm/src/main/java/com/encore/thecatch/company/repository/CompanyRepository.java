package com.encore.thecatch.company.repository;

import com.encore.thecatch.company.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
