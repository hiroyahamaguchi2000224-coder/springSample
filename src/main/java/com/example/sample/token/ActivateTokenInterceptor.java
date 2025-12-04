package com.example.sample.token;

import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

/**
 * ActivateTokenアノテーションを処理するインターセプタ。
 * <p>
 * CREATE: トークンを生成しセッションに保持。<br>
 * VALIDATE: トークンを検証し、不正な場合はInvalidTokenExceptionをスロー。
 */
@Slf4j
public class ActivateTokenInterceptor implements HandlerInterceptor {

    /** セッションに格納するトークンのキー名（リクエストパラメータ名と統一） */
    public static final String TOKEN_KEY = "_token";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        if (handler instanceof HandlerMethod hm) {
            ActivateToken ann = hm.getMethodAnnotation(ActivateToken.class);
            if (ann == null) {
                return true;
            }
            
            HttpSession session = request.getSession(false);
            
            if (ann.type() == ActivateToken.TokenType.CREATE) {
                // トークン生成モード
                String token = createToken(request);
                log.debug("トークン生成: {}", token);
                
            } else if (ann.type() == ActivateToken.TokenType.VALIDATE) {
                // トークン検証モード
                if (session == null) {
                    log.warn("セッションが存在しません");
                    throw new InvalidTokenException("セッションが無効です。");
                }
                
                String submittedToken = request.getParameter(TOKEN_KEY);
                if (!validateToken(session, submittedToken)) {
                    log.warn("トークン検証失敗: submitted={}", submittedToken);
                    throw new InvalidTokenException("不正なトークンです。");
                }
                
                log.debug("トークン検証成功");
                
                // 検証成功後、自動的に新しいトークンを生成
                String newToken = createToken(request);
                log.debug("トークン再生成: {}", newToken);
            }
        }
        return true;
    }
    
    /**
     * トークンを生成しセッションに保持。
     * 
     * @param request HTTPリクエスト
     * @return 生成されたトークン
     */
    private String createToken(HttpServletRequest request) {
        return TokenHelper.createTokenInfo(request, TOKEN_KEY);
    }
    
    /**
     * トークンを検証。
     * <p>
     * セッションに保持されたトークンと送信されたトークンを比較。
     * 検証後は呼び出し元で新トークンを自動生成する。
     * 
     * @param session HTTPセッション
     * @param submittedToken 送信されたトークン
     * @return 検証成功時true
     */
    private boolean validateToken(HttpSession session, String submittedToken) {
        if (!StringUtils.hasText(submittedToken)) {
            return false;
        }
        
        Object sessionToken = session.getAttribute(TOKEN_KEY);
        if (sessionToken == null) {
            return false;
        }
        
        return sessionToken.equals(submittedToken);
    }
}
