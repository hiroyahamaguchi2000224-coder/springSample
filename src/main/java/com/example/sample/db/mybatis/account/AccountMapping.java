package com.example.sample.db.mybatis.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Account テーブルのマッピングクラス（MyBatis用）。
 * <p>
 * テーブル: USERS
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountMapping {
    private String userId;
    private String password;
    private String userName;
    private String role;
    private Boolean delFlg;
    private Boolean accountLocked;
}
