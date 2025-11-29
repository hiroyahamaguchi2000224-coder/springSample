package com.example.sample.config;

import com.example.sample.mapper.AccountMapper;
import com.example.sample.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Spring Security 認証成功時のハンドラ
 * <p>
 * 認証成功後、ユーザー情報をセッションに保存し、メニュー画面へリダイレクトする。
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final AccountMapper accountMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        HttpSession session = request.getSession(true);

        // 認証済みプリンシパル（ユーザー名）を取得し、DB からユーザー名を補完してセッションに保存
        String username = authentication.getName();
        session.setAttribute("userId", username);

        Account account = accountMapper.findByUserId(username);
        if (account != null && account.getUserName() != null) {
            session.setAttribute("userName", account.getUserName());
        } else {
            session.setAttribute("userName", username);
        }

        // メニュー画面にリダイレクト
        response.sendRedirect(request.getContextPath() + "/vz0102");
    }
}
