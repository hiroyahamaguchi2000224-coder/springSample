package com.example.sample.controller.vb0101;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.sample.controller.vz0102.VZ0102Controller;
import com.example.sample.token.ActivateToken;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * VB0101 VB業務処理画面 Controller
 * 画面説明などは日本語で記載し、用語は英語（Controller/Service/Form）を使用
 */
@Slf4j
@Controller
@RequestMapping(VB0101Controller.PATH)
@RequiredArgsConstructor
public class VB0101Controller {
    /** コントローラのベースパス */
    public static final String PATH = "/vb0101";
    /** 表示するテンプレート名 */
    private static final String VIEW = "pages" + PATH + "/index";
    /** 自画面へのリダイレクト定数（外部から参照可能） */
    public static final String REDIRECT = "redirect:" + PATH;
    /** フォーム名定数（ModelAttributeとHTMLで統一） */
    public static final String FORM = "vb0101Form";

    
    /**
     * VB0101 VB業務処理画面 初期表示
     * VB業務処理画面を表示する
     *
     * @param form VB業務処理 Form（Spring が自動的に Model に追加）
     * @return 表示するテンプレート名
     */
    @GetMapping
    @ActivateToken(type = ActivateToken.TokenType.CREATE)
    public String init(@ModelAttribute(FORM) VB0101Form form) {
        
        // ログインユーザー名を取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        
        // Formに設定
        form.setUserName(userName);
        
        return VIEW;
    }

    /**
     * VB0101 VB業務処理画面 検索
     * 検索条件に基づいてデータを検索する
     *
     * @param form VB業務処理 Form（Spring が自動的に Model に追加）
     * @return 表示するテンプレート名
     */
    @PostMapping("/search")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String search(VB0101Form form) {
        
        // ログインユーザー名を取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        // ログインユーザ名の画面表示
        form.setParameter(userName);
        
        return VIEW;
    }

    /**
     * VB0101 VB業務処理画面 選択
     * 一覧から項目を選択する
     *
     * @param form VB業務処理 Form（Spring が自動的に Model に追加）
     * @return 表示するテンプレート名
     */
    @PostMapping("/select")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String select(VB0101Form form) {
        
        // ログインユーザー名を取得
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();

        // ログインユーザ名の画面表示
        form.setParameter(userName);
        
        return VZ0102Controller.REDIRECT;
    }

    /**
     * VB0101 VB業務処理画面 実行処理
     * 
     * @param form VB業務処理 Form（Spring が自動的に Model に追加）
     * @param model ビューへ渡すモデル（メッセージ表示に使用）
     * @return 表示するテンプレート名
     */
    @PostMapping("/execute")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String execute(VB0101Form form, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = auth.getName();
        
        // Formに設定
        form.setUserName(userName);
        
        // メッセージはメッセージクラス経由の表示なのでそのまま
        model.addAttribute("message", "実行処理は未実装です");
        return VIEW;
    }

    /**
     * VB0101 VB業務処理画面 クリア処理
     * 
     * @param form VB業務処理 Form（Spring が自動的に Model に追加）
     * @return リダイレクト先
     */
    @PostMapping("/clear")
    @ActivateToken(type = ActivateToken.TokenType.VALIDATE)
    public String clear(VB0101Form form) {
        return REDIRECT;
    }

}
