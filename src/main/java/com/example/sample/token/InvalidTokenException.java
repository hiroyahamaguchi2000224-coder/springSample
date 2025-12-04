package com.example.sample.token;

/**
 * 二重送信防止トークンのエラー例外。
 * <p>
 * トークンが不正・期限切れ・存在しない場合にスローされる。
 */
public class InvalidTokenException extends RuntimeException {
    
    public InvalidTokenException(String message) {
        super(message);
    }
    
    public InvalidTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
