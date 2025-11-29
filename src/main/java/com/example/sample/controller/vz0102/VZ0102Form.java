package com.example.sample.controller.vz0102;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VZ0102 メニュー画面用フォーム
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VZ0102Form {
    /**
     * メニュー選択時のアクション名
     */
    private String menuAction;
}
