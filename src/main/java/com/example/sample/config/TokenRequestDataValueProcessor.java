package com.example.sample.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.web.servlet.support.csrf.CsrfRequestDataValueProcessor;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

import com.example.sample.token.ActivateTokenInterceptor;

import jakarta.servlet.http.HttpServletRequest;

/**
 * CSRFトークンと独自の二重送信防止トークンを自動挿入するプロセッサ。
 * <p>
 * Spring SecurityのCsrfRequestDataValueProcessorをラップし、
 * getExtraHiddenFieldsで独自トークン(_token)も追加する。
 * th:action を使用したフォームに対して自動的にhiddenフィールドを挿入する。
 * <p>
 * Bean登録は {@link RequestDataValueProcessorAutoConfig} で行われる。
 * Spring標準のRequestDataValueProcessorを上書きするため、
 * application.ymlで {@code spring.main.allow-bean-definition-overriding=true} を設定している。
 */
public class TokenRequestDataValueProcessor implements RequestDataValueProcessor {

    private final CsrfRequestDataValueProcessor csrfProcessor;

    public TokenRequestDataValueProcessor() {
        this.csrfProcessor = new CsrfRequestDataValueProcessor();
    }

    @Override
    @NonNull
    public String processAction(@NonNull HttpServletRequest request, @NonNull String action, @NonNull String httpMethod) {
        // CSRF処理をそのまま委譲
        return Objects.requireNonNull(csrfProcessor.processAction(request, action, httpMethod));
    }

    @Override
    @NonNull
    public String processFormFieldValue(@NonNull HttpServletRequest request, @Nullable String name, @NonNull String value, @NonNull String type) {
        // CSRF処理をそのまま委譲
        return Objects.requireNonNull(csrfProcessor.processFormFieldValue(request, name, value, type));
    }

    @Override
    @NonNull
    public Map<String, String> getExtraHiddenFields(@NonNull HttpServletRequest request) { // NOSONAR @NonNullApiがパッケージレベルで型パラメータもカバー
        // CSRFトークンを取得
        Map<String, String> hiddenFields = csrfProcessor.getExtraHiddenFields(request);
        
        // 可変マップに変換(念のため)
        Map<String, String> mutableFields = new HashMap<>(hiddenFields != null ? hiddenFields : Map.of());
        
        // 独自トークン(_token)を追加
        Object tokenVal = request.getAttribute(ActivateTokenInterceptor.TOKEN_KEY);
        if (tokenVal != null) {
            mutableFields.put(ActivateTokenInterceptor.TOKEN_KEY, tokenVal.toString());
        }
        
        return mutableFields;
    }

    @Override
    @NonNull
    public String processUrl(@NonNull HttpServletRequest request, @NonNull String url) {
        // CSRF処理をそのまま委譲
        return Objects.requireNonNull(csrfProcessor.processUrl(request, url));
    }
}
