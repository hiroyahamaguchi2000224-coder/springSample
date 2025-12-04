package com.example.sample.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.sample.token.InvalidTokenException;
import com.example.sample.util.Message;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

/**
 * 共通エクセプションハンドラ。
 * <p>
 * 各機能単位でキャッチできないエラーを処理し、共通エラー画面（VZ0103）へリダイレクトする。
 * 重大なエラー時のみに使用し、ユーザーは画面を閉じるかログアウトすることしかできない。
 */
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String REDIRECT_VZ0103 = "redirect:/vz0103";
    
    private final Message message;
    
    /**
     * `ServiceException` を処理し、共通エラー画面へリダイレクトする。
     *
     * @param e 発生した `ServiceException`
     * @param request HTTP リクエスト情報
     * @param redirectAttributes リダイレクト属性（フラッシュスコープ）
     * @return 共通エラー画面へのリダイレクト
     */
    @ExceptionHandler(ServiceException.class)
    public String handleServiceException(ServiceException e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String uri = request.getRequestURI();
        log.error("ServiceException補捉: code={}, message={}, uri={}", 
            e.getErrorCode(), e.getMessage(), uri, e);
        
        // Serviceで設定済みのメッセージをフラッシュスコープに追加
        redirectAttributes.addFlashAttribute("message", e.getMessage());
        redirectAttributes.addFlashAttribute("errorScreen", uri);
        return REDIRECT_VZ0103;
    }
    
    /**
     * `UserNotFoundException` を処理し、共通エラー画面へリダイレクトする。
     *
     * @param e 発生した `UserNotFoundException`
     * @param request HTTP リクエスト情報
     * @param redirectAttributes リダイレクト属性（フラッシュスコープ）
     * @return 共通エラー画面へのリダイレクト
     */
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String uri = request.getRequestURI();
        log.error("UserNotFoundException補捉: message={}, uri={}", 
            e.getMessage(), uri, e);
        
        message.addFlashErrorMessage(redirectAttributes, "E0100", uri);
        return REDIRECT_VZ0103;
    }
    
    /**
     * `InvalidTokenException` を処理し、共通エラー画面へリダイレクトする。
     * <p>
     * 二重送信防止トークンが不正または期限切れの場合のエラー処理。
     *
     * @param e 発生した `InvalidTokenException`
     * @param request HTTP リクエスト情報
     * @param redirectAttributes リダイレクト属性（フラッシュスコープ）
     * @return 共通エラー画面へのリダイレクト
     */
    @ExceptionHandler(InvalidTokenException.class)
    public String handleInvalidTokenException(InvalidTokenException e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String uri = request.getRequestURI();
        log.warn("InvalidTokenException補捉: message={}, uri={}", 
            e.getMessage(), uri);
        
        message.addFlashErrorMessage(redirectAttributes, "E0102", uri);
        return REDIRECT_VZ0103;
    }
    
    /**
     * `DataAccessException` を処理し、共通エラー画面へリダイレクトする。
     * <p>
     * データベースアクセスで発生したエラーの処理。
     *
     * @param e 発生した `DataAccessException`
     * @param request HTTP リクエスト情報
     * @param redirectAttributes リダイレクト属性（フラッシュスコープ）
     * @return 共通エラー画面へのリダイレクト
     */
    @ExceptionHandler(DataAccessException.class)
    public String handleDataAccessException(DataAccessException e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String uri = request.getRequestURI();
        log.error("DataAccessException補捉: message={}, uri={}", 
            e.getMessage(), uri, e);
        
        message.addFlashErrorMessage(redirectAttributes, "E0002", uri);
        return REDIRECT_VZ0103;
    }
    
    /**
     * 予期しない例外を処理し、共通エラー画面へリダイレクトする。
     * <p>
     * ここでは詳細をログ出力し、共通エラー画面へ遷移させる。
     *
     * @param e 発生した例外
     * @param request HTTP リクエスト情報
     * @param redirectAttributes リダイレクト属性（フラッシュスコープ）
     * @return 共通エラー画面へのリダイレクト
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String uri = request.getRequestURI();
        log.error("リクエスト {} で例外発生: {}", 
            uri, e.getClass().getName(), e);
        log.error("例外詳細: message={}", e.getMessage());
        
        message.addFlashErrorMessage(redirectAttributes, "E0001", uri);
        return REDIRECT_VZ0103;
    }
}
