package com.example.sample.db.jpa.cart;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

/**
 * カートマスタエンティティ（JPA用）。
 * <p>
 * テーブル: CART
 */
@Entity
@Table(name = "TT_CART", schema = "SAMPLE")
@IdClass(CartId.class)
public class CartEntity {
    
    @Id
    @Column(name = "USER_ID", nullable = false, length = 50)
    private String userId;
    
    @Id
    @Column(name = "PRODUCT_ID", nullable = false, length = 50)
    private String productId;
    
    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;
    
    @Column(name = "DEL_FLG", nullable = false)
    private Boolean delFlg;
    
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
    
    public CartEntity() {
    }
    
    public CartEntity(String userId, String productId, Integer quantity, Boolean delFlg, 
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.delFlg = delFlg;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    public Boolean getDelFlg() {
        return delFlg;
    }
    
    public void setDelFlg(Boolean delFlg) {
        this.delFlg = delFlg;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

