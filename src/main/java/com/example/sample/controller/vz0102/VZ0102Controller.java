package com.example.sample.controller.vz0102;

import com.example.sample.service.MenuService;
import com.example.sample.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpSession;
import java.util.List;

/**
 * VZ0102 メニュー画面コントローラ
 */
@Controller
@RequestMapping(VZ0102Controller.PATH)
@RequiredArgsConstructor
public class VZ0102Controller {
    /** コントローラのベースパス */
    public static final String PATH = "/vz0102";
    /** 表示するテンプレート名 */
    public static final String VIEW = "pages/vz0102/index";
    /** 自画面へのリダイレクト定数（外部から参照可能） */
    public static final String REDIRECT = "redirect:" + PATH;
    /** ログアウト処理アクション */
    public static final String ACTION_LOGOUT = "/logout";

    private final MenuService menuService;
    
    /**
     * メニュー画面の表示
     *
     * @param model ビューへ渡すモデル
     * @param session HTTP セッション（ユーザー情報を参照）
     * @return 表示するテンプレート名またはリダイレクト文字列
     */
    @GetMapping
    public String show(Model model, HttpSession session) {
        // セッションからユーザー情報取得
        String userName = (String) session.getAttribute("userName");
        
        if (userName == null) {
            return REDIRECT;
        }
        
        List<MenuService.MenuDTO> menus = menuService.getAllMenus();
        
        model.addAttribute("userName", userName);
        model.addAttribute("menus", menus);
        model.addAttribute("vZ0102Form", new VZ0102Form());
        
        return VIEW;
    }
    
    /**
     * ログアウト処理
     *
     * @param session ログアウト対象の HTTP セッション
     * @return リダイレクト先のパス
     */
    @GetMapping(ACTION_LOGOUT)
    public String logout(HttpSession session) {
        session.invalidate();
        return REDIRECT;
    }
    
    /**
     * メニュー画面内での `ServiceException` を処理する。
     *
     * @param e 発生した `ServiceException`
     * @param model ビューへ渡すモデル
     * @param session HTTP セッション
     * @return メニュー画面のテンプレート名（エラーメッセージ付き）
     */
    @ExceptionHandler(ServiceException.class)
    public String handleServiceException(ServiceException e, Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        
        if (userName == null) {
            return REDIRECT;
        }
        
        List<MenuService.MenuDTO> menus = menuService.getAllMenus();
        
        model.addAttribute("userName", userName);
        model.addAttribute("menus", menus);
        model.addAttribute("error", e.getMessage());
        model.addAttribute("vZ0102Form", new VZ0102Form());
        
        return VIEW;
    }
}
