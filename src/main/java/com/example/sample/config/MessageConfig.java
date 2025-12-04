package com.example.sample.config;

import java.util.Locale;
import java.util.Objects;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * メッセージ管理設定。
 * <p>
 * - messages.properties: アプリケーション共通メッセージ（エラーコードなど）
 * - ValidationMessages.properties: Bean Validation メッセージ
 */
@Configuration
public class MessageConfig {

    /**
     * アプリケーション共通メッセージ用 MessageSource。
     * エラーコードからメッセージを取得する際に使用。
     * 
     * @return MessageSource
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.JAPANESE);
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    /**
     * Bean Validation 用 Validator。
     * ValidationMessages.properties を使用してバリデーションエラーメッセージを解決。
     * 
     * @return LocalValidatorFactoryBean
     */
    @Bean
    public LocalValidatorFactoryBean validator() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setValidationMessageSource(
            Objects.requireNonNull(messageSource(), "MessageSource must not be null")
        );
        return validator;
    }
}
