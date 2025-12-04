package com.example.sample.util;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;

/**
 * メッセージ管理ユーティリティクラス。
 * <p>
 * - リダイレクト時: フラッシュスコープにメッセージを追加（addFlashMessage）
 * - 画面遷移なし: Model にメッセージを追加（addMessage）
 * <p>
 * 各画面では message フラグメントで ${message} を表示。
 */
@Component
@RequiredArgsConstructor
public class Message {
    
    private final MessageSource messageSource;
    private static final String ATTR_MESSAGE = "message";
    
    /**
     * フラッシュスコープにメッセージを追加する。
     * <p>
     * messages.properties からメッセージコードでメッセージを取得し、
     * RedirectAttributes の FlashAttribute に "message" として設定。
     * リダイレクト後の画面で ${message} として表示される。
     *
     * @param redirectAttributes リダイレクト属性
     * @param messageCode メッセージコード（messages.properties のキー）
     */
    @SuppressWarnings("null")
    public void addFlashMessage(RedirectAttributes redirectAttributes, String messageCode) {
        redirectAttributes.addFlashAttribute(ATTR_MESSAGE, 
            messageSource.getMessage(messageCode, null, null));
    }
    
    /**
     * フラッシュスコープにメッセージを追加する（引数付き）。
     * <p>
     * messages.properties のプレースホルダー {0}, {1}, ... を引数で置換。
     *
     * @param redirectAttributes リダイレクト属性
     * @param messageCode メッセージコード（messages.properties のキー）
     * @param args メッセージ引数
     */
    @SuppressWarnings("null")
    public void addFlashMessage(RedirectAttributes redirectAttributes, String messageCode, Object[] args) {
        redirectAttributes.addFlashAttribute(ATTR_MESSAGE, 
            messageSource.getMessage(messageCode, args, null));
    }
    
    /**
     * エラーメッセージをフラッシュスコープに追加する。
     * <p>
     * VZ0103 共通エラー画面用。発生画面名も同時に設定。
     *
     * @param redirectAttributes リダイレクト属性
     * @param messageCode エラーメッセージコード
     * @param errorScreen 発生画面名（URI）
     */
    @SuppressWarnings("null")
    public void addFlashErrorMessage(RedirectAttributes redirectAttributes, String messageCode, String errorScreen) {
        redirectAttributes.addFlashAttribute(ATTR_MESSAGE, 
            messageSource.getMessage(messageCode, null, null));
        redirectAttributes.addFlashAttribute("errorScreen", errorScreen);
    }
    
    /**
     * Model にメッセージを追加する（画面遷移なし）。
     * <p>
     * リダイレクトせず、同じ画面でメッセージを表示する場合に使用。
     * バリデーションエラーなどで入力画面に戻る際に使用。
     *
     * @param model モデル
     * @param messageCode メッセージコード（messages.properties のキー）
     */
    @SuppressWarnings("null")
    public void addMessage(Model model, String messageCode) {
        model.addAttribute(ATTR_MESSAGE, 
            messageSource.getMessage(messageCode, null, null));
    }
    
    /**
     * Model にメッセージを追加する（引数付き、画面遷移なし）。
     * <p>
     * messages.properties のプレースホルダー {0}, {1}, ... を引数で置換。
     *
     * @param model モデル
     * @param messageCode メッセージコード（messages.properties のキー）
     * @param args メッセージ引数
     */
    @SuppressWarnings("null")
    public void addMessage(Model model, String messageCode, Object[] args) {
        model.addAttribute(ATTR_MESSAGE, 
            messageSource.getMessage(messageCode, args, null));
    }
    
    /**
     * Model にエラーメッセージを追加する（画面遷移なし）。
     * <p>
     * 同じ画面でエラーメッセージを表示する場合に使用。
     *
     * @param model モデル
     * @param messageCode エラーメッセージコード
     */
    @SuppressWarnings("null")
    public void addErrorMessage(Model model, String messageCode) {
        model.addAttribute(ATTR_MESSAGE, 
            messageSource.getMessage(messageCode, null, null));
    }
}
