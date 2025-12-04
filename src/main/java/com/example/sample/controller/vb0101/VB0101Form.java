package com.example.sample.controller.vb0101;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VB0101 - VB業務処理画面のフォームクラス。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VB0101Form {
    
    /** ログインユーザー名 */
    private String userName;
    
    /** 処理パラメータ */
    private String parameter;
}
