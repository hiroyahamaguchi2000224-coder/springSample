package com.example.sample.db.jpa.company;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 会社マスタ用 JPA Repository。
 */
@Repository
public interface CompanyRepository extends JpaRepository<CompanyEntity, String> {
}
