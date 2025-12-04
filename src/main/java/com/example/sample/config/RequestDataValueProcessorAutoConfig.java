package com.example.sample.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.support.RequestDataValueProcessor;

/**
 * RequestDataValueProcessorの自動設定クラス。
 * <p>
 * Spring Securityの設定後に実行されることを保証し、
 * CSRFトークンと独自の二重送信防止トークン(_token)を
 * Thymeleafフォームに自動挿入する。
 * <p>
 * {@link SecurityAutoConfiguration}の後に実行されるため、
 * Spring SecurityのデフォルトCSRF設定を上書きせずに拡張できる。
 */
@AutoConfiguration(after = SecurityAutoConfiguration.class)
@ConditionalOnWebApplication
public class RequestDataValueProcessorAutoConfig {

    /**
     * CSRFトークンと独自トークンを自動挿入するプロセッサを生成する。
     * <p>
     * Thymeleafのフォーム(th:action)に対して、CSRFトークン(_csrf)と
     * 独自の二重送信防止トークン(_token)を自動的にhiddenフィールドとして挿入する。
     * <p>
     * このBeanはSpring Securityの設定後に登録されるため、
     * SecurityのデフォルトRequestDataValueProcessorを適切に上書きできる。
     * 
     * @return RequestDataValueProcessor インスタンス
     */
    @Bean
    public RequestDataValueProcessor requestDataValueProcessor() {
        return new TokenRequestDataValueProcessor();
    }
}
