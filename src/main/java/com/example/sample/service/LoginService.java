package com.example.sample.service;

import com.example.sample.entity.Account;
import com.example.sample.mapper.AccountMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ログイン関連の認証用ロジックを提供するサービス。
 * <p>
 * Spring Security の `UserDetailsService` を実装し、認証フローに統合する。
 * 注意: Spring Security 側の `PasswordEncoder`（`BCryptPasswordEncoder` を想定）と
 * DB に保存されているパスワードの形式が一致している必要があります。
 */
@Service
@RequiredArgsConstructor
public class LoginService implements UserDetailsService {

    private final AccountMapper accountMapper;

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
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("ユーザーIDが指定されていません");
        }

        Account account = accountMapper.findByUserId(username);
        if (account == null) {
            throw new UsernameNotFoundException("ユーザーが見つかりません: " + username);
        }

        String role = account.getRole() == null ? "ROLE_USER" : account.getRole();
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));

        // 返却する UserDetails のパスワードは DB に保存されている形式のままとする。
        // そのため、SecurityConfig に登録する PasswordEncoder と DB の保存形式を合わせてください。
        return User.withUsername(account.getUserId())
                .password(account.getPassword())
                .authorities(authorities)
                .build();
    }
}
