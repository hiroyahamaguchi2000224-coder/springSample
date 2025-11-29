package com.example.sample.exception;

public class UserNotFoundException extends RuntimeException {
    /**
     * 指定ユーザーが見つからないことを示す例外。
     */
    public UserNotFoundException(String message) {
        super(message);
    }
    
    /**
     * 原因を指定して例外を生成する。
     *
     * @param message エラーメッセージ
     * @param cause 原因例外
     */
    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
