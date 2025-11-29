package com.example.sample.exception;

/**
 * 各種画面内でキャッチされる想定のカスタム例外。
 */
public class ServiceException extends RuntimeException {
    
    private String errorCode;
    
    /**
     * デフォルトのエラーメッセージで例外を生成する。
     *
     * @param message エラーメッセージ
     */
    public ServiceException(String message) {
        super(message);
        this.errorCode = "E001";
    }
    
    /**
     * エラーコード付きで例外を生成する。
     *
     * @param errorCode エラーコード
     * @param message エラーメッセージ
     */
    public ServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * 原因例外をラップして例外を生成する。
     *
     * @param message エラーメッセージ
     * @param cause 原因となった例外
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "E001";
    }
    
    /**
     * エラーコードと原因例外を指定して生成する。
     *
     * @param errorCode エラーコード
     * @param message エラーメッセージ
     * @param cause 原因例外
     */
    public ServiceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * エラーコードを取得する。
     *
     * @return エラーコード文字列
     */
    public String getErrorCode() {
        return errorCode;
    }
}
