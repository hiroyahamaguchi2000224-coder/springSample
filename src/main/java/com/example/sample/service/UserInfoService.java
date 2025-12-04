package com.example.sample.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * ユーザー情報取得 Service
 * ログインユーザー情報の取得を担当。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserInfoService {
       
    /**
     * ユーザー情報 DTO
     * Serviceで取得したユーザー情報をControllerに渡すためのデータ転送オブジェクト。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDto {
        /** ログインユーザー名 */
        private String userName;
    }

    /**
     * 現在ログインしているユーザーの情報を取得する。
     * 
     * @return UserInfoDto ユーザー情報
     */
    public UserInfoDto getUserInfo() {
        // 認証情報からユーザー名を取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        
        log.debug("ログインユーザー名を取得: {}", userName);
        
        // DTOに設定して返却
        return new UserInfoDto(userName);
    }
}
