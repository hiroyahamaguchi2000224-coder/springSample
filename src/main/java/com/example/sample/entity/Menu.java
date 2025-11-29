package com.example.sample.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * メニュー項目を表すエンティティ。
 * <p>
 * MyBatisで使用するPOJOクラス。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Menu {
    private Long id;
    private String screenId;
    private String screenName;
    private String buttonName;
    private Integer displayOrder;
}
