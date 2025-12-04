package com.example.sample.controller.vz0103;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.sample.token.ActivateToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * VZ0103 共通エラー画面 Controller
 * 画面説明などは日本語で記載し、用語は英語（Controller/Service/Form）を使用
 */
@Slf4j
@Controller
@RequestMapping(VZ0103Controller.PATH)
@RequiredArgsConstructor
public class VZ0103Controller {
    /** コントローラのベースパス */
    public static final String PATH = "/vz0103";
    /** 表示するテンプレート名 */
    private static final String VIEW = "pages" + PATH + "/index";
    /** 自画面へのリダイレクト定数（外部から参照可能） */
    public static final String REDIRECT = "redirect:" + PATH;
    /** フォーム名定数（ModelAttributeとHTMLで統一） */
    public static final String FORM = "vz0103Form";

    /**
     * VZ0103 共通エラー画面 初期表示
     * 共通エラー画面を表示する
     * 
     * @return エラー画面テンプレート
     */
    @GetMapping
    @ActivateToken(type = ActivateToken.TokenType.CREATE)
    public String init() {
        return VIEW;
    }
}
