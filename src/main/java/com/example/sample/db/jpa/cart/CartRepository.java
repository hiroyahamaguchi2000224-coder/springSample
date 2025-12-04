package com.example.sample.db.jpa.cart;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * カートマスタ用 JPA Repository。
 */
@Repository
public interface CartRepository extends JpaRepository<CartEntity, CartId> {
    
    /**
     * ユーザIDでカート内商品を検索する。
     *
     * @param userId ユーザID
     * @return カートエンティティのリスト
     */
    List<CartEntity> findByUserId(String userId);
    
    /**
     * ユーザIDと商品IDでカート内商品を検索する。
     *
     * @param userId ユーザID
     * @param productId 商品ID
     * @return カートエンティティ
     */
    Optional<CartEntity> findByUserIdAndProductId(String userId, String productId);
}
