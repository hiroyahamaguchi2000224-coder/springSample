package com.example.sample.controller.vz0102;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.sample.controller.va0101.VA0101Controller;
import com.example.sample.controller.vb0101.VB0101Controller;
import com.example.sample.controller.vz9901.VZ9901Controller;
import com.example.sample.controller.vz9902.VZ9902Controller;
import com.example.sample.service.UserInfoService;
import com.example.sample.service.UserInfoService.UserInfoDto;
import com.example.sample.token.ActivateToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * VZ0102 メニュー画面 Controller
 * 画面説明などは日本語で記載し、用語は英語（Controller/Service/Form）を使用
 */
@Slf4j
@Controller
@RequestMapping(VZ0102Controller.PATH)
@RequiredArgsConstructor
public class VZ0102Controller {
    /** コントローラのベースパス */
    public static final String PATH = "/vz0102";
    /** 表示するテンプレート名 */
    private static final String VIEW = "pages" + PATH + "/index";
    /** 自画面へのリダイレクト定数（外部から参照可能） */
    public static final String REDIRECT = "redirect:" + PATH;
    /** フォーム名定数(ModelAttributeとHTMLで統一) */
    public static final String FORM = "vz0102Form";
    
    private final UserInfoService userInfoService;
    private final ModelMapper modelMapper;

    /**
     * VZ0102 メニュー画面 初期表示
     * Serviceでユーザー名を取得し、ModelMapperでFormにマッピング
     *
     * @param form VZ0102Form(Springが自動作成、@ModelAttributeで明示的に名前指定)
     * @return 表示するテンプレート名
     */
    @GetMapping
    @ActivateToken(type = ActivateToken.TokenType.CREATE)
    public String init(@ModelAttribute(FORM) VZ0102Form form) {
        // Serviceからユーザー情報を取得
        UserInfoDto dto = userInfoService.getUserInfo();
        
        // ModelMapperでDto → Form
        modelMapper.map(dto, form);
        
        return VIEW;
    }

    /**
     * VA0101 商品検索画面への遷移（PRGパターン）
     */
    @PostMapping("/selectVa0101")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String selectVa0101() {
        return VA0101Controller.REDIRECT;
    }

    /**
     * VB0101 VB業務処理画面への遷移（PRGパターン）
     */
    @PostMapping("/selectVb0101")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String selectVb0101() {
        return VB0101Controller.REDIRECT;
    }

    /**
     * VZ9901 ドライバ画面への遷移(PRGパターン)
     */
    @PostMapping("/selectVz9901")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String selectVz9901() {
        return VZ9901Controller.REDIRECT;
    }

    /**
     * VZ9902 モックアップ画面への遷移(PRGパターン)
     */
    @PostMapping("/selectVz9902")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String selectVz9902() {
        return VZ9902Controller.REDIRECT;
    }
}
