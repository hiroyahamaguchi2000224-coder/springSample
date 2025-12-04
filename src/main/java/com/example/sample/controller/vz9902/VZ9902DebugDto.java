package com.example.sample.controller.vz9902;

import java.util.Map;

import lombok.Data;

/**
 * VZ9902 デバッグ情報DTO。
 * ModelMapperでFormにマッピングするための中間DTO
 */
@Data
public class VZ9902DebugDto {
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
    
    /** Flash属性（PRGパターン） */
    private Map<String, Object> flashAttributes;
    
    /** リクエストパラメータ */
    private Map<String, String> requestParams;
    
    /** HTTPヘッダー */
    private Map<String, String> headers;
}
