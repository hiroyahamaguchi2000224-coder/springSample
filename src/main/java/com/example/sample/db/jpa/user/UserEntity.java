package com.example.sample.db.jpa.user;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザエンティティ（JPA用）。
 * <p>
 * テーブル: USERS
 */
@Entity
@Table(name = "TM_USER", schema = "SAMPLE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    
    @Id
    @Column(name = "USER_ID", nullable = false, length = 50)
    private String userId;
    
    @Column(name = "PASSWORD", nullable = false, length = 255)
    private String password;
    
    @Column(name = "USER_NAME", nullable = false, length = 100)
    private String userName;
    
    @Column(name = "ROLE", nullable = false, length = 50)
    private String role;
    
    @Column(name = "ACCOUNT_LOCKED", nullable = false)
    private Boolean accountLocked;
    
    @Column(name = "DEL_FLG", nullable = false)
    private Boolean delFlg;
    
    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;
}
