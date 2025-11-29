package com.example.sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.sample.controller.vz0101.VZ0101Controller;
import com.example.sample.service.LoginService;

import lombok.RequiredArgsConstructor;

/**
 * セキュリティ設定クラス。
 * <p>
 * 認可ルール、フォーム認証、ログアウト、CSRF/ヘッダ設定などを構成する。
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
        private final LoginService loginService;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    /**
     * セキュリティフィルタチェーンを構築する。
     *
     * @param http `HttpSecurity` の設定オブジェクト
     * @return 設定済みの `SecurityFilterChain`
     * @throws Exception セキュリティ設定の構築に失敗した場合
     */
     SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // ルートパス
                        .requestMatchers("/").permitAll()
                        // ログイン画面
                    .requestMatchers(VZ0101Controller.PATH, VZ0101Controller.PATH + "/**").permitAll()

                        // ログイン失敗用エラー画面
                        .requestMatchers("/vz0103/**").permitAll()
                        // 共通レイアウト
                        .requestMatchers("/layout/**").permitAll()
                        // フラグメント（ヘッダー・メッセージエリアなど）
                        .requestMatchers("/fragments/**").permitAll()
                        // 静的リソース
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        // 開発用 H2 コンソール
                        .requestMatchers("/h2-console/**").permitAll()
                        // その他は認証必須
                        .anyRequest().authenticated())

                .userDetailsService(loginService)
                .formLogin(form -> form
                        .loginPage(VZ0101Controller.PATH) // ログイン画面
                        .loginProcessingUrl("/login")
                        .successHandler(customAuthenticationSuccessHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl(VZ0101Controller.PATH)
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**"))
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}
