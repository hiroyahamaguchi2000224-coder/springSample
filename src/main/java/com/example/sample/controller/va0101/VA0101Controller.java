package com.example.sample.controller.va0101;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.sample.token.ActivateToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * VA0101 商品検索画面 Controller
 * 画面説明などは日本語で記載し、用語は英語（Controller/Service/Form）を使用
 */
@Slf4j
@Controller
@RequestMapping(VA0101Controller.PATH)
@RequiredArgsConstructor
public class VA0101Controller {
    /** コントローラのベースパス */
    public static final String PATH = "/va0101";
    /** 表示するテンプレート名 */
    private static final String VIEW = "pages" + PATH + "/index";
    /** 自画面へのリダイレクト定数（外部から参照可能） */
    public static final String REDIRECT = "redirect:" + PATH;
    /** フォーム名定数（ModelAttributeとHTMLで統一） */
    public static final String FORM = "va0101Form";

    /**
     * VA0101 商品検索画面 初期表示
     * 商品検索画面を表示する（初期表示時は検索結果なし）
     *
     * @param form 商品検索 Form（Spring が自動的に Model に追加）
     * @return 表示するテンプレート名
     */
    @GetMapping
    @ActivateToken(type = ActivateToken.TokenType.CREATE)
    public String init(@ModelAttribute(FORM) VA0101Form form) {
        return VIEW;
    }

    /**
     * VA0101 商品検索画面 検索処理
     * 
     * @param form 商品検索 Form（Spring が自動的に Model に追加）
     * @param model ビューへ渡すモデル（メッセージ表示に使用）
     * @return 表示するテンプレート名
     */
    @PostMapping("/search")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String search(VA0101Form form, Model model) {
        model.addAttribute("message", "検索処理は未実装です");
        return VIEW;
    }

    /**
     * VA0101 商品検索画面 クリア処理
     * 
     * @param form 商品検索 Form（Spring が自動的に Model に追加）
     * @return リダイレクト先
     */
    @PostMapping("/clear")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String clear(VA0101Form form) {
        return REDIRECT;
    }

    /**
     * VA0101 商品検索画面 カート追加処理
     * 
     * @param form 商品検索 Form（Spring が自動的に Model に追加）
     * @param model ビューへ渡すモデル（メッセージ表示に使用）
     * @return 表示するテンプレート名
     */
    @PostMapping("/addToCart")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String addToCart(VA0101Form form, Model model) {
        model.addAttribute("message", "カート追加処理は未実装です");
        return VIEW;
    }
}
