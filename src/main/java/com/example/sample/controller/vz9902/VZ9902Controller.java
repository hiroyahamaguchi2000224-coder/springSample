package com.example.sample.controller.vz9902;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * VZ9902 モックアップ画面 Controller。
 * <p>
 * 全画面からフォワードして、セッション・リクエスト・送信情報を表示するデバッグ用画面。
 */
@Slf4j
@Controller
@RequestMapping(VZ9902Controller.PATH)
@RequiredArgsConstructor
public class VZ9902Controller {
    /** Controller のベースパス */
    public static final String PATH = "/vz9902";
    /** View のテンプレートパス */
    private static final String VIEW = "pages" + PATH + "/index";
        /** 自画面へのリダイレクト定数（外部から参照可能） */
    public static final String REDIRECT = "redirect:" + PATH;
    /** フォーム名定数（ModelAttributeとHTMLで統一） */
    public static final String FORM = "vz9902Form";
    
    /**
     * VZ9902 モックアップ画面を表示する（GET）。
     *
     * @param form VZ9902Form（Springが自動的にModelに追加）
     * @param model モデル（Flash属性のメッセージ表示に使用）
     * @param request HTTP リクエスト
     * @param session HTTP セッション
     * @return ビュー名
     */
    @GetMapping
    public String show(
            @ModelAttribute(FORM) VZ9902Form form, 
            Model model, 
            HttpServletRequest request, 
            HttpSession session) {
        log.debug("VZ9902Controller.show() GET called");
        populateDebugInfo(form, model, request, session);
        return VIEW;
    }
    
    /**
     * VZ9902 モックアップ画面を表示する（POST）。
     * PRGパターンでRedirectし、Flash属性でパラメータを渡す
     *
     * @param form VZ9902Form
     * @param model モデル
     * @param request HTTP リクエスト
     * @param session HTTP セッション
     * @param redirectAttributes リダイレクト属性（Flashスコープ）
     * @return リダイレクト先
     */
    @PostMapping
    public String showPost(
            @ModelAttribute(FORM) VZ9902Form form,
            Model model,
            HttpServletRequest request,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        log.debug("VZ9902Controller.show() POST called");
        
        // POSTパラメータをすべて取得
        Map<String, String> postParams = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            String[] values = request.getParameterValues(name);
            postParams.put(name, values.length == 1 ? values[0] : String.join(", ", values));
        }
        
        // POSTで受け取ったパラメータをFlash属性に設定
        redirectAttributes.addFlashAttribute("postedParams", postParams);
        redirectAttributes.addFlashAttribute("infoMessage", "POSTリクエストを受け取りました（PRGパターン）");
        
        log.debug("POST params: {}", postParams);
        
        // PRGパターン: POST後はRedirect
        return REDIRECT;
    }
    
    /**
     * デバッグ情報をFormに設定する。
     *
     * @param form VZ9902Form
     * @param model モデル（Flash属性のメッセージ表示に使用）
     * @param request HTTP リクエスト
     * @param session HTTP セッション
     */
    private void populateDebugInfo(VZ9902Form form, Model model, HttpServletRequest request, HttpSession session) {
        // セッション情報
        Map<String, Object> sessionAttributes = new HashMap<>();
        Enumeration<String> sessionAttrNames = session.getAttributeNames();
        while (sessionAttrNames.hasMoreElements()) {
            String name = sessionAttrNames.nextElement();
            sessionAttributes.put(name, session.getAttribute(name));
        }
        
        // リクエスト属性（Flash属性のみ表示）
        Map<String, Object> flashAttributes = new HashMap<>();
        Enumeration<String> requestAttrNames = request.getAttributeNames();
        while (requestAttrNames.hasMoreElements()) {
            String name = requestAttrNames.nextElement();
            // Flash属性のみ抽出（org.springframework.web.servlet.FlashMap.FLASH_MAP_ATTRIBUTE由来）
            if (name.equals("postedParams") || name.equals("infoMessage")) {
                flashAttributes.put(name, request.getAttribute(name));
            }
        }
        
        // リクエストパラメータ（すべて取得：_csrf, _token含む）
        Map<String, String> allRequestParams = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            String[] values = request.getParameterValues(name);
            // 複数値の場合はカンマ区切りで結合
            allRequestParams.put(name, values.length == 1 ? values[0] : String.join(", ", values));
        }
        
        // リクエストヘッダー情報
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            headers.put(name, request.getHeader(name));
        }
        
        // Formに設定
        form.setMethod(request.getMethod());
        form.setRequestURI(request.getRequestURI());
        form.setQueryString(request.getQueryString());
        form.setSessionId(session.getId());
        form.setSessionAttributes(sessionAttributes);
        form.setFlashAttributes(flashAttributes);
        form.setRequestParams(allRequestParams);  // HttpServletRequestから取得した全パラメータを設定
        form.setHeaders(headers);
        
        // Flash属性（postedParams, infoMessage）はそのままmodelに残す（メッセージ表示用）
        if (request.getAttribute("postedParams") != null) {
            model.addAttribute("postedParams", request.getAttribute("postedParams"));
        }
        if (request.getAttribute("infoMessage") != null) {
            model.addAttribute("infoMessage", request.getAttribute("infoMessage"));
        }
        
        log.debug("Session attributes: {}", sessionAttributes);
        log.debug("Flash attributes: {}", flashAttributes);
        log.debug("Request params (all): {}", allRequestParams);
    }
}
