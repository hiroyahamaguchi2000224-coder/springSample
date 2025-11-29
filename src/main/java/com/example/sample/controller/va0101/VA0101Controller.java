package com.example.sample.controller.va0101;

import com.example.sample.service.VA0101Service;
import com.example.sample.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpSession;

/**
 * VA0101 VA機能画面コントローラ
 */
@Controller
@RequestMapping(VA0101Controller.PATH)
@RequiredArgsConstructor
public class VA0101Controller {
    /** コントローラのベースパス */
    public static final String PATH = "/va0101";
    /** このコントローラが返すビュー名 */
    public static final String VIEW = "pages/va0101/index";
    /** 自画面へのリダイレクト定数（外部から参照可能） */
    public static final String REDIRECT = "redirect:" + PATH;
    /** 処理実行アクション */
    public static final String ACTION_EXECUTE = "/execute";

    private final VA0101Service va0101Service;
    
    /**
     * VA0101画面の表示
     *
     * @param model ビューへ渡すモデル
     * @param session HTTP セッション（ユーザー情報参照）
     * @return 表示するテンプレート名またはリダイレクト
     */
    @GetMapping
    public String show(Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        
        if (userName == null) {
            return REDIRECT;
        }
        
        model.addAttribute("userName", userName);
        model.addAttribute("vA0101Form", new VA0101Form());
        return VIEW;
    }
    
    /**
     * VA0101の処理実行
     *
     * @param form フォーム入力
     * @param model ビューへ渡すモデル
     * @param session HTTP セッション
     * @return 表示するテンプレート名またはリダイレクト
     */
    @PostMapping(ACTION_EXECUTE)
    public String execute(VA0101Form form, Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        
        if (userName == null) {
            return REDIRECT;
        }
        
        // サービスでビジネスロジック実行（例外があればexecuteHandlerで処理）
        va0101Service.executeVA(form.getParameter());
        
        model.addAttribute("userName", userName);
        model.addAttribute("message", "VA0101処理が完了しました。");
        model.addAttribute("vA0101Form", new VA0101Form());
        return VIEW;
    }
    
    /**
     * VA0101画面内での `ServiceException` を処理する。
     *
     * @param e 発生した `ServiceException`
     * @param model ビューへ渡すモデル
     * @param session HTTP セッション
     * @return VA0101画面のテンプレート名（エラーメッセージ付き）
     */
    @ExceptionHandler(ServiceException.class)
    public String handleServiceException(ServiceException e, Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        model.addAttribute("userName", userName);
        model.addAttribute("error", e.getMessage());
        model.addAttribute("vA0101Form", new VA0101Form());
        return VIEW;
    }
}
