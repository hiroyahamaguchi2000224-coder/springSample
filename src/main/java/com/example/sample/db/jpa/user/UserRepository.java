package com.example.sample.db.jpa.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ユーザーマスタ用 JPA Repository。
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {
    
    /**
     * ユーザIDで検索する。
     *
     * @param userId ユーザID
     * @return ユーザエンティティ
     */
    Optional<UserEntity> findByUserId(String userId);
}
