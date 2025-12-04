package com.example.sample.db.jpa.usercompanyproductauth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ユーザ所属会社別購入可能商品承認用 JPA Repository。
 */
@Repository
public interface UserCompanyProductAuthRepository extends JpaRepository<UserCompanyProductAuthEntity, CompanyProductAuthId> {
}
