package com.example.sample.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * アカウント情報を表すエンティティ。
 * <p>
 * MyBatisで使用するPOJOクラス。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Long id;
    private String userId;
    private String password;
    private String userName;
    private String role;
}
