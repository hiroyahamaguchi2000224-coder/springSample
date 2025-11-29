package com.example.sample.controller.vz0101;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * VZ0101 ログイン画面コントローラ
 * <p>
 * ログイン画面の表示のみを担当。
 * 認証処理は Spring Security が処理し、成功時の挙動は
 * {@link com.example.sample.config.CustomAuthenticationSuccessHandler} で実装する。
 */
@Controller
@RequestMapping(VZ0101Controller.PATH)
@RequiredArgsConstructor
public class VZ0101Controller {
    /** コントローラのベースパス（外部から参照可能） */
    public static final String PATH = "/vz0101";
    /** ログイン画面のテンプレート名 */
    public static final String VIEW = "pages/vz0101/index";

    /** ログイン画面へのリダイレクト定数（外部から参照可能） */
    public static final String REDIRECT = "redirect:" + PATH;

    /**
     * ログイン画面の表示
     *
     * @param model ビューへ渡すモデル
     * @return ログイン画面のテンプレート名
     */
    @GetMapping
    public String show(Model model) {
        return VIEW;
    }
}
