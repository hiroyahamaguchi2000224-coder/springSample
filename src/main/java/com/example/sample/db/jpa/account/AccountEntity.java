package com.example.sample.db.jpa.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Account テーブルのエンティティ（JPA用）。
 * <p>
 * テーブル: USERS
 */
@Entity
@Table(name = "TM_USER", schema = "SAMPLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
    
    @Id
    @Column(name = "USER_ID", nullable = false, length = 50)
    private String userId;
    
    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;
    
    @Column(name = "USER_NAME", length = 100)
    private String userName;
    
    @Column(name = "ROLE", length = 50)
    private String role;
    
    @Column(name = "DEL_FLG")
    private Boolean delFlg;
    
    @Column(name = "ACCOUNT_LOCKED")
    private Boolean accountLocked;
}
