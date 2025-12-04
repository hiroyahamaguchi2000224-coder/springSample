package com.example.sample.db.jpa.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品マスタエンティティ（JPA用）。
 * <p>
 * テーブル: PRODUCT
 */
@Entity
@Table(name = "TM_PRODUCT", schema = "SAMPLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    
    @Id
    @Column(name = "PRODUCT_ID", nullable = false, length = 50)
    private String productId;
    
    @Column(name = "PRODUCT_NAME", nullable = false, length = 200)
    private String productName;
    
    @Column(name = "COMPANY_ID", nullable = false, length = 20)
    private String companyId;
    
    @Column(name = "PRICE", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(name = "STOCK_QUANTITY", nullable = false)
    private Integer stockQuantity;
    
    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "DEL_FLG", nullable = false)
    private Boolean delFlg;
    
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
