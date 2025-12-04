package com.example.sample;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.extern.slf4j.Slf4j;

/**
 * パスワードハッシュ検証ツール（テスト用）。
 * <p>
 * BCryptパスワードエンコーダーを使用して、平文パスワードと
 * 既存のハッシュ値の検証、および新しいハッシュ値の生成を行う。
 * <p>
 * 実行例：
 * <pre>
 * java com.example.sample.PasswordHashGenerator password123
 * </pre>
 * 
 * @see BCryptPasswordEncoder
 */
@Slf4j
public class PasswordHashGenerator {
    
    /**
     * メインメソッド。
     * <p>
     * コマンドライン引数で指定された平文パスワードを使用して：
     * <ol>
     * <li>既存のハッシュ値との照合検証</li>
     * <li>新しいハッシュ値の生成と検証</li>
     * </ol>
     * を実行する。
     * 
     * @param args コマンドライン引数（args[0]に検証したい平文パスワード）
     */
    public static void main(String[] args) {
        if (args.length < 1 || args[0] == null || args[0].isBlank()) {
            log.error("検証したい平文パスワードを引数で指定してください。");
            System.exit(1);
        }

        String candidate = args[0];
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // テスト用サンプルハッシュ（password123）
        // 本番ではLoginServiceがUserRepositoryを通じてDBから実際のハッシュを取得
        String storedHash = "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYzOW8m8aP2"; // NOSONAR テスト用サンプルハッシュ
        log.info("=== データベースのハッシュ検証（サンプル） ===");
        log.info("ハッシュ: {}", storedHash);
        log.info("matches('{}'): {}", "[hidden]", encoder.matches(candidate, storedHash)); // NOSONAR テスト用サンプルハッシュ

        String newHash = encoder.encode(candidate);
        log.info("=== 入力値のハッシュ生成 ===");
        log.info("新しいハッシュ: {}", newHash);
        log.info("検証: {}", encoder.matches(candidate, newHash));
    }
}
