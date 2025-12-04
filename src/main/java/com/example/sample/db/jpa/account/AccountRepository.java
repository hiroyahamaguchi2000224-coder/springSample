package com.example.sample.db.jpa.account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Account テーブル用 JPA Repository。
 * <p>
 * 将来的にJPAを使用する場合のために準備。
 * 現在はMyBatisを使用しているため未使用。
 */
@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, String> {
    
    /**
     * ユーザーIDで検索。
     *
     * @param userId ユーザーID
     * @return AccountEntity
     */
    Optional<AccountEntity> findByUserId(String userId);
}
