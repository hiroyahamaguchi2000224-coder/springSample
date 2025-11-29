package com.example.sample.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザーエンティティ。
 * <p>
 * MyBatisで使用するPOJOクラス。
 * DBの`users`テーブルとマッピングされる。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /** ユーザーの識別子（主キー） */
    private Long id;
    /** ユーザー名（ログインIDではなく表示名/識別名） */
    private String username;
    /** メールアドレス */
    private String email;
    /** ハッシュ化済みパスワード */
    private String password;
}
