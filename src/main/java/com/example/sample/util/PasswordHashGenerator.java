package com.example.sample.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.extern.slf4j.Slf4j;

/**
 * BCryptパスワードハッシュ生成ツール。
 * <p>
 * コマンドラインから平文パスワードを受け取り、
 * BCryptアルゴリズム（strength=12）でハッシュ化して返す。
 * <p>
 * 実行例：
 * <pre>
 * java com.example.sample.util.PasswordHashGenerator myPassword123
 * </pre>
 * 
 * @see BCryptPasswordEncoder
 */
@Slf4j
public class PasswordHashGenerator {
    
    /**
     * メインメソッド。
     * <p>
     * コマンドライン引数から平文パスワードを受け取り、
     * BCryptハッシュを生成して表示。
     * SQL UPDATE文も合わせて出力する。
     * 
     * @param args コマンドライン引数（args[0]に平文パスワード）
     */
    public static void main(String[] args) {
        if (args.length == 0 || args[0] == null || args[0].isBlank()) {
            log.error("平文パスワードを引数で指定してください。例: java ... PasswordHashGenerator mySecret");
            System.exit(1);
        }

        String plainPassword = args[0];
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String hashedPassword = encoder.encode(plainPassword);

        log.info("{}", "=".repeat(60));
        log.info("BCrypt Password Hash Generator (strength=12)");
        log.info("{}", "=".repeat(60));
        log.info("Plain text: [hidden] (引数で受領)");
        log.info("Hashed:     {}", hashedPassword);
        log.info("Length:     {}", hashedPassword.length());
        log.info("{}", "=".repeat(60));
        log.info("SQL UPDATE statement:");
        log.info("UPDATE users SET password = '{}' WHERE user_id = '<replace>';", hashedPassword);
        log.info("{}", "=".repeat(60));
    }
}
