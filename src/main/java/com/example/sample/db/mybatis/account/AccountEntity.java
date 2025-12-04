package com.example.sample.db.mybatis.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * アカウント情報を表すエンティティ（MyBatis用）。
 * <p>
 * テーブル: USERS
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountEntity {
    private String userId;
    private String password;
    private String userName;
    private String role;
    private Boolean delFlg;
    private Boolean accountLocked;
}
