package com.example.sample.config;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.example.sample.token.ActivateTokenInterceptor;
import com.example.sample.token.TokenHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * カスタム認証失敗ハンドラー。
 * <p>
 * セキュリティ向上のため、認証失敗の詳細（ユーザー不存在、パスワード不一致など）を
 * 攻撃者に伝えず、統一的なエラーメッセージを表示する。
 * また、ログイン失敗時に新しいトークンを生成してリダイレクト先で利用可能にする。
 */
@Slf4j
@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public CustomAuthenticationFailureHandler() {
        // 失敗時のリダイレクト先を設定
        setDefaultFailureUrl("/vz0101?error");
    }

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        
        // 詳細なエラーログは記録するが、ユーザーには表示しない
        log.warn("認証失敗: IP={}, User={}, Reason={}", 
            request.getRemoteAddr(),
            request.getParameter("username"),
            exception.getClass().getSimpleName());
        
        // ログイン失敗時に新しいトークンを生成（キー名は「_token」）
        String newToken = TokenHelper.createTokenInfo(request, ActivateTokenInterceptor.TOKEN_KEY);
        log.debug("ログイン失敗時の新トークン生成: {}", newToken);
        
        // セキュリティ上、詳細情報を隠蔽した統一メッセージでリダイレクト
        // ユーザー名の存在チェックやパスワード不一致の区別をさせない
        super.onAuthenticationFailure(request, response, exception);
    }
}
