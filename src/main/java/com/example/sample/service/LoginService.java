package com.example.sample.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sample.db.jpa.user.UserEntity;
import com.example.sample.db.jpa.user.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * ログイン関連の認証用ロジックを提供する Service。
 * <p>
 * Spring Security の `UserDetailsService` を実装し、認証フローに統合する。
 * JPA (UserRepository) を使用してユーザー情報を取得する。
 */
@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);
    private final UserRepository userRepository;

    /**
     * Spring Security 用のユーザー情報読み込み。
     *
     * @param username ログインユーザー名
     * @return `UserDetails`
     * @throws UsernameNotFoundException ユーザー未登録時
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("認証試行: username={}", username);
        
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("ユーザーIDが指定されていません");
        }

        UserEntity user = userRepository.findByUserId(username)
            .orElseThrow(() -> {
                log.warn("ユーザーが見つかりません: {}", username);
                return new UsernameNotFoundException("ユーザーが見つかりません: " + username);
            });

        log.debug("ユーザー取得成功: userId={}, delFlg={}, locked={}", 
            user.getUserId(), user.getDelFlg(), user.getAccountLocked());

        // アカウントが削除済みまたはロックされている場合
        if (user.getDelFlg() != null && user.getDelFlg()) {
            log.warn("アカウントが削除済み: {}", username);
            throw new UsernameNotFoundException("アカウントが削除されています: " + username);
        }
        if (user.getAccountLocked() != null && user.getAccountLocked()) {
            log.warn("アカウントがロック: {}", username);
            throw new UsernameNotFoundException("アカウントがロックされています: " + username);
        }

        String role = user.getRole() == null ? "ROLE_USER" : user.getRole();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        log.debug("UserDetails作成: username={}, role={}, passwordLength={}", 
            user.getUserId(), role, user.getPassword() != null ? user.getPassword().length() : 0);

        // Spring Security標準のUserオブジェクトを返す
        return User.withUsername(user.getUserId())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
