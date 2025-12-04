package com.example.sample.controller.va0101;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VA0101 VA機能画面用フォーム
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VA0101Form {
    /**
     * 商品名（検索条件）
     */
    private String productName;
    
    /**
     * 商品コード（検索条件）
     */
    private String productCode;
}
