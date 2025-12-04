package com.example.sample.token;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * フォーム二重送信防止用トークン生成/検証を有効化するためのアノテーション。
 * <p>HandlerMethod に付与し、CREATE 指定時はセッションへトークンを生成。
 * VALIDATE 指定時は後段のフィルタ/インターセプタで検証処理が行われる基盤を用意する。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ActivateToken {
    TokenType type() default TokenType.VALIDATE;
    enum TokenType { CREATE, VALIDATE }
}
