package com.example.sample.db.jpa.usercompanyproductauth;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会社別購入可能商品承認エンティティ（JPA用）。
 * <p>
 * テーブル: COMPANY_PRODUCT_AUTH
 */
@Entity
@Table(name = "TM_COMPANY_PRODUCT_AUTH", schema = "SAMPLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(CompanyProductAuthId.class)
public class UserCompanyProductAuthEntity {
    
    @Id
    @Column(name = "COMPANY_ID", nullable = false, length = 20)
    private String companyId;
    
    @Id
    @Column(name = "SALES_COMPANY_ID", nullable = false, length = 20)
    private String salesCompanyId;
    
    @Column(name = "DEL_FLG", nullable = false)
    private Boolean delFlg;
    
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
