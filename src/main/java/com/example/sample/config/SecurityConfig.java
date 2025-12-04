package com.example.sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;

import com.example.sample.controller.vz0101.VZ0101Controller;
import com.example.sample.controller.vz0102.VZ0102Controller;
import com.example.sample.controller.vz0103.VZ0103Controller;
import com.example.sample.controller.vz9901.VZ9901Controller;
import com.example.sample.controller.vz9902.VZ9902Controller;
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

        private final LoginService loginService;
        private final CustomAuthenticationFailureHandler failureHandler;

        /**
         * BCryptPasswordEncoderをDIコンテナに登録する。
         * <p>
         * Spring Securityの認証処理で自動的に使用される。
         * strength=12でセキュリティ強度を高めている（デフォルトは10）。
         * 
         * @return BCryptPasswordEncoderインスタンス
         */
        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder(12);
        }

        /**
         * セキュリティフィルタチェーンを構築する。
         *
         * @param http `HttpSecurity` の設定オブジェクト
         * @return 設定済みの `SecurityFilterChain`
         * @throws Exception セキュリティ設定の構築に失敗した場合
         */
        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // ルートパス（認証済みならメニューへ、未認証ならログインへリダイレクト）
                        .requestMatchers("/").permitAll()
                        // ログイン画面
                        .requestMatchers(VZ0101Controller.PATH, VZ0101Controller.PATH + "/**").permitAll()

                        // ログイン失敗用エラー画面
                        .requestMatchers(VZ0103Controller.PATH, VZ0103Controller.PATH + "/**").permitAll()

                        // TODO: 開発用画面へのアクセス制御（本番では削除または管理者のみ許可などに変更） // NOSONAR
                        // ドライバ画面（開発用）
                        .requestMatchers(VZ9901Controller.PATH, VZ9901Controller.PATH + "/**").authenticated()
                        // モックアップ画面（開発用）
                        .requestMatchers(VZ9902Controller.PATH, VZ9902Controller.PATH + "/**").authenticated()
                        
                        // 共通レイアウト
                        .requestMatchers("/layout/**").permitAll()
                        // フラグメント（ヘッダー・メッセージエリアなど）
                        .requestMatchers("/fragments/**").permitAll()
                        // 静的リソース
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        // その他は認証必須
                        .anyRequest().authenticated())

                .userDetailsService(loginService)
                .formLogin(form -> form
                        .loginPage(VZ0101Controller.PATH)
                        .loginProcessingUrl("/login")
                        .failureHandler(failureHandler) // 統一エラーメッセージのためカスタムハンドラー使用
                        .defaultSuccessUrl(VZ0102Controller.PATH, true)
                        .permitAll())
                // 未認証ユーザーのアクセスはログイン画面へリダイレクト
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendRedirect(VZ0101Controller.PATH)))
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl(VZ0101Controller.PATH + "?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll())
                // CSRF保護を有効化（デフォルト）
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")) // H2コンソールのみ除外（本番では不要）
                // セッション管理: セッション固定攻撃対策
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession() // ログイン成功時にセッションIDを再生成
                        .maximumSessions(1) // 同時ログインセッション数1つまで
                        .maxSessionsPreventsLogin(false)) // 新しいログインで古いセッションを無効化
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }

        /**
         * StrictHttpFirewall の微調整。
         * <p>
         * デフォルトでは URL 内のセミコロン (;) を拒否するが、ブラウザが Cookie を拒否した場合に
         * セッションIDを URL リライト方式 (";jsessionid=") で付加することがあり、そのケースを許容する。
         * 本番で Cookie が確実に利用できる場合は allowSemicolon(false) のままでも良い。
         */
        @Bean
        public HttpFirewall httpFirewall() {
                StrictHttpFirewall firewall = new StrictHttpFirewall();
                firewall.setAllowSemicolon(true); // ;jsessionid を許可
                return firewall;
        }
}
