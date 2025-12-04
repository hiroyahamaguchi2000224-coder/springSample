package com.example.sample.controller.vz9902;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VZ9902 - モックアップ画面のフォームクラス。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VZ9902Form {
    
    /** HTTPメソッド */
    private String method;
    
    /** リクエストURI */
    private String requestURI;
    
    /** クエリ文字列 */
    private String queryString;
    
    /** セッションID */
    private String sessionId;
    
    /** セッション属性 */
    private Map<String, Object> sessionAttributes;
    
    /** Flash属性 */
    private Map<String, Object> flashAttributes;
    
    /** リクエストパラメータ */
    private Map<String, String> requestParams;
    
    /** リクエストヘッダー */
    private Map<String, String> headers;
}
