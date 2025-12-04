package com.example.sample.db.jpa.product;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 商品マスタ用 JPA Repository。
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, String> {
    
    /**
     * ユーザの所属会社が購入可能な商品を検索する。
     *
     * @param companyId ユーザの所属会社ID
     * @param productName 商品名（部分一致、null可）
     * @return 商品エンティティのリスト
     */
    @Query("SELECT p FROM ProductEntity p " +
           "WHERE p.delFlg = false " +
           "AND p.companyId IN (" +
           "  SELECT ucpa.salesCompanyId FROM UserCompanyProductAuthEntity ucpa " +
           "  WHERE ucpa.companyId = :companyId AND ucpa.delFlg = false" +
           ") " +
           "AND (:productName IS NULL OR p.productName LIKE %:productName%) " +
           "ORDER BY p.productId")
    List<ProductEntity> findAvailableProducts(
        @Param("companyId") String companyId,
        @Param("productName") String productName
    );
}
