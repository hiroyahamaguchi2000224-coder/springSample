package com.example.sample.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import lombok.extern.slf4j.Slf4j;

/**
 * BCryptパスワードハッシュ生成・検証ツール（テスト用）。
 * <p>
 * コマンドラインから平文パスワードを受け取り、
 * BCryptハッシュを生成し、即座に検証する。
 * <p>
 * 実行例：
 * <pre>
 * java com.example.sample.util.GenerateHash testPassword
 * </pre>
 * 
 * @see BCryptPasswordEncoder
 */
@Slf4j
public class GenerateHash {
    
    /**
     * メインメソッド。
     * <p>
     * 平文パスワードからBCryptハッシュを生成し、
     * 生成したハッシュで元のパスワードを検証できることを確認する。
     * SQL UPDATEコマンドも合わせて出力する。
     * 
     * @param args コマンドライン引数（args[0]に平文パスワード）
     */
    public static void main(String[] args) {
        if (args.length == 0 || args[0] == null || args[0].isBlank()) {
            log.error("平文パスワードを引数で指定してください。");
            System.exit(1);
        }
        String password = args[0];
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode(password);
        
        log.info("Generated Hash: {}", hash);
        log.info("Verification: {}", encoder.matches(password, hash));
        log.info("SQL Update Command: UPDATE account SET password = '{}' WHERE user_id = '<replace>';", hash);
    }
}
