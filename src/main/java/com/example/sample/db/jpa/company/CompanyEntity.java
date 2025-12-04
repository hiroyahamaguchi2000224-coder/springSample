package com.example.sample.db.jpa.company;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会社マスタエンティティ（JPA用）。
 * <p>
 * テーブル: COMPANY
 */
@Entity
@Table(name = "TM_COMPANY", schema = "SAMPLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyEntity {
    
    @Id
    @Column(name = "COMPANY_ID", nullable = false, length = 20)
    private String companyId;
    
    @Column(name = "COMPANY_NAME", nullable = false, length = 200)
    private String companyName;
    
    @Column(name = "DEL_FLG", nullable = false)
    private Boolean delFlg;
    
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
