package com.example.sample.controller.vz9901;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.sample.controller.va0101.VA0101Controller;
import com.example.sample.controller.vb0101.VB0101Controller;
import com.example.sample.controller.vz0101.VZ0101Controller;
import com.example.sample.controller.vz0102.VZ0102Controller;
import com.example.sample.controller.vz0103.VZ0103Controller;
import com.example.sample.controller.vz9902.VZ9902Controller;
import com.example.sample.token.ActivateToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * VZ9901 ドライバ画面 Controller
 * 全画面へのリンクを提供する開発用ドライバ画面
 */
@Slf4j
@Controller
@RequestMapping(VZ9901Controller.PATH)
@RequiredArgsConstructor
public class VZ9901Controller {
    /** コントローラのベースパス */
    public static final String PATH = "/vz9901";
    /** 表示するテンプレート名 */
    private static final String VIEW = "pages" + PATH + "/index";
    /** 自画面へのリダイレクト定数（外部から参照可能） */
    public static final String REDIRECT = "redirect:" + PATH;
    /** フォーム名定数（ModelAttributeとHTMLで統一） */
    public static final String FORM = "vz9901Form";
    
    /**
     * VZ9901 ドライバ画面 初期表示
     *
     * @param form VZ9901Form(Springが自動作成)
     * @return 表示するテンプレート名
     */
    @GetMapping
    @ActivateToken(type = ActivateToken.TokenType.CREATE)
    public String init(@ModelAttribute(FORM) VZ9901Form form) {
        log.debug("VZ9901Controller.init() called");
        return VIEW;
    }

    // ========== VZ: 共通機能 ==========
    
    @PostMapping("/selectVz0101")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String selectVz0101() {
        return VZ0101Controller.REDIRECT;
    }

    @PostMapping("/selectVz0102")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String selectVz0102() {
        return VZ0102Controller.REDIRECT;
    }

    @PostMapping("/selectVz0103")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String selectVz0103() {
        return VZ0103Controller.REDIRECT;
    }

    @PostMapping("/selectVz9901")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String selectVz9901() {
        return REDIRECT;
    }

    @PostMapping("/selectVz9902")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String selectVz9902() {
        return VZ9902Controller.REDIRECT;
    }

    // ========== VA: 購買機能 ==========
    
    @PostMapping("/selectVa0101")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String selectVa0101() {
        return VA0101Controller.REDIRECT;
    }

    // ========== VB: サンプル機能 ==========
    
    @PostMapping("/selectVb0101")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String selectVb0101() {
        return VB0101Controller.REDIRECT;
    }
}
