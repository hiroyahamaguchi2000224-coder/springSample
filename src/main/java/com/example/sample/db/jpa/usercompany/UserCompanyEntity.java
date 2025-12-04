package com.example.sample.db.jpa.usercompany;

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
 * ユーザ所属マスタエンティティ（JPA用）。
 * <p>
 * テーブル: USER_COMPANY
 */
@Entity
@Table(name = "TM_USER_COMPANY", schema = "SAMPLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
@IdClass(UserCompanyId.class)
public class UserCompanyEntity {
    
    @Id
    @Column(name = "USER_ID", nullable = false, length = 50)
    private String userId;
    
    @Id
    @Column(name = "COMPANY_ID", nullable = false, length = 20)
    private String companyId;
    
    @Column(name = "DEL_FLG", nullable = false)
    private Boolean delFlg;
    
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
