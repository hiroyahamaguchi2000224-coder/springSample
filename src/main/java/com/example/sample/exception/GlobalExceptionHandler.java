package com.example.sample.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * 共通エクセプションハンドラ。
 * <p>
 * 各機能単位でキャッチできないエラーを処理し、エラー画面（VZ0103）へ遷移させる。
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * エラー情報 DTO（inner class）。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDTO {
        /** エラーコード */
        private String errorCode;
        /** 表示するエラーメッセージ */
        private String errorMessage;
        /** エラー発生時刻 */
        private LocalDateTime timestamp;
        /** リクエスト URI */
        private String requestUri;
    }
    
    /**
     * `ServiceException` を処理し、エラー画面に必要な情報をモデルへ設定する。
     *
     * @param e 発生した `ServiceException`
     * @param model ビューへ渡すモデル
     * @param request HTTP リクエスト情報
     * @return 遷移先のテンプレート名
     */
    @ExceptionHandler(ServiceException.class)
    public String handleServiceException(ServiceException e, Model model, HttpServletRequest request) {
        GlobalExceptionHandler.ErrorDTO errorDTO = new GlobalExceptionHandler.ErrorDTO();
        errorDTO.setErrorCode(e.getErrorCode());
        errorDTO.setErrorMessage(e.getMessage());
        errorDTO.setTimestamp(LocalDateTime.now());
        errorDTO.setRequestUri(request.getRequestURI());
        
        model.addAttribute("error", errorDTO);
        return "vz0103-error";
    }
    
    /**
     * `UserNotFoundException` を処理するハンドラ。
     *
     * @param e 発生した `UserNotFoundException`
     * @param model ビューへ渡すモデル
     * @param request HTTP リクエスト情報
     * @return エラー画面のテンプレート名
     */
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException e, Model model, HttpServletRequest request) {
        GlobalExceptionHandler.ErrorDTO errorDTO = new GlobalExceptionHandler.ErrorDTO();
        errorDTO.setErrorCode("E002");
        errorDTO.setErrorMessage(e.getMessage());
        errorDTO.setTimestamp(LocalDateTime.now());
        errorDTO.setRequestUri(request.getRequestURI());
        
        model.addAttribute("error", errorDTO);
        return "vz0103-error";
    }
    
    /**
     * 予期しない例外を処理するハンドラ。
     * <p>
     * ここでは詳細をログ出力し、一般的なエラーメッセージを画面に表示する。
     *
     * @param e 発生した例外
     * @param model ビューへ渡すモデル
     * @param request HTTP リクエスト情報
     * @return エラー画面のテンプレート名
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, Model model, HttpServletRequest request) {
        GlobalExceptionHandler.ErrorDTO errorDTO = new GlobalExceptionHandler.ErrorDTO();
        errorDTO.setErrorCode("E999");
        errorDTO.setErrorMessage("予期しないエラーが発生しました。システム管理者にお問い合わせください。");
        errorDTO.setTimestamp(LocalDateTime.now());
        errorDTO.setRequestUri(request.getRequestURI());
        
        // ログに詳細を出力
        e.printStackTrace();
        
        model.addAttribute("error", errorDTO);
        model.addAttribute("detailMessage", e.getMessage());
        return "vz0103-error";
    }
}
