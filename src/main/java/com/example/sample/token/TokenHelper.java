package com.example.sample.token;

import java.math.BigInteger;
import java.security.SecureRandom;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * トークン生成用ヘルパークラス。
 * <p>
 * セキュアな乱数を使用してトークン文字列を生成する。
 */
public final class TokenHelper {
    
    private static final SecureRandom RANDOM = new SecureRandom();
    
    private TokenHelper() {
        // インスタンス化禁止
    }
    
    /**
     * 36進数のランダムトークン文字列を生成する。
     * <p>
     * 165ビットの乱数をBase36エンコードして返却。
     * 
     * @return 生成されたトークン文字列
     */
    public static String generateToken() {
        return new BigInteger(165, RANDOM).toString(36);
    }
    
    /**
     * トークン情報を作成し、セッションとリクエスト属性に保存する。
     * <p>
     * セッション: トークン検証時に使用<br>
     * リクエスト属性: RequestDataValueProcessorでフォームに自動挿入
     * 
     * @param request HTTPリクエスト
     * @param sessionKey セッション属性のキー名
     * @return 生成されたトークン文字列
     */
    public static String createTokenInfo(HttpServletRequest request, String sessionKey) {
        HttpSession session = request.getSession(true);
        String token = generateToken();
        session.setAttribute(sessionKey, token);
        request.setAttribute(sessionKey, token);
        return token;
    }
}
