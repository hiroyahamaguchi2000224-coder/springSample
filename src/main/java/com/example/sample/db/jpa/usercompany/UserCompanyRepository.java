package com.example.sample.db.jpa.usercompany;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ユーザ所属マスタRepository（JPA用）。
 */
@Repository
public interface UserCompanyRepository extends JpaRepository<UserCompanyEntity, UserCompanyId> {
}
