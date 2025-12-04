package com.example.sample.controller.vz0101;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.sample.token.ActivateToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * VZ0101 ログイン画面 Controller
 * 画面説明などは日本語で記載し、用語は英語（Controller/Service/Form）を使用
 * ※ログイン画面はFormを使用しないため、FORM定数は定義しない
 */
@Slf4j
@Controller
@RequestMapping(VZ0101Controller.PATH)
@RequiredArgsConstructor
public class VZ0101Controller {
    /** コントローラのベースパス */
    public static final String PATH = "/vz0101";
    /** 表示するテンプレート名 */
    private static final String VIEW = "pages" + PATH + "/index";
    /** 自画面へのリダイレクト定数（外部から参照可能） */
    public static final String REDIRECT = "redirect:" + PATH;
    /**
     * VZ0101 ログイン画面 初期表示
     * ログイン画面を表示する
     * 
     * @return ログイン画面テンプレート
     */
    @GetMapping
    @ActivateToken(type = ActivateToken.TokenType.CREATE)
    public String init() {
        return VIEW;
    }
}
