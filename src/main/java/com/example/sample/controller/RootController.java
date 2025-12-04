package com.example.sample.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.sample.controller.vz0101.VZ0101Controller;
import com.example.sample.controller.vz0102.VZ0102Controller;

import lombok.extern.slf4j.Slf4j;

/**
 * ルート Controller
 * 画面説明などは日本語で記載し、用語は英語（Controller/Service/Form）を使用
 * <p>
 * ルートパス（/）へのアクセスを適切な画面にリダイレクトする。
 */
@Slf4j
@Controller
public class RootController {

    /**
     * ルートパスへのアクセス処理
     * <p>
     * 認証済みの場合はメニュー画面へ、未認証の場合はログイン画面へリダイレクト
     *
     * @return リダイレクト先のパス
     */
    @GetMapping("/")
    public String root() {
        log.debug("RootController.root() called");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // 認証済みかつ匿名ユーザーでない場合
        if (auth != null && auth.isAuthenticated() && 
            !"anonymousUser".equals(auth.getPrincipal())) {
            return VZ0102Controller.REDIRECT;
        }
        
        // 未認証の場合
        return VZ0101Controller.REDIRECT;
    }
}
