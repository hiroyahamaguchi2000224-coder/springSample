package com.example.sample.config;

import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.example.sample.token.ActivateTokenInterceptor;

/**
 * Web MVC統合設定クラス。
 * <p>
 * ViewController登録、インターセプタ登録などの基本的なMVC設定を担当。
 * トークン自動挿入は {@link RequestDataValueProcessorAutoConfig} が担当。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // ==== ViewController (現在未使用) ====
    @Override
    public void addViewControllers(@NonNull ViewControllerRegistry registry) {
        // 必要になればここへ静的ビュー追加（例: 利用規約ページなど）
    }

    // ==== 二重送信防止トークン関連 Bean ====
    /**
     * 二重送信防止トークンのインターセプタを生成する。
     * 
     * @return ActivateTokenInterceptor インスタンス
     */
    @Bean
    public ActivateTokenInterceptor activateTokenInterceptor() {
        return new ActivateTokenInterceptor();
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(Objects.requireNonNull(activateTokenInterceptor()));
    }

    // ==== ModelMapper ====
    /**
     * ModelMapperインスタンスを生成する。
     * DTO/Entity間のマッピングに使用。
     * 
     * @return ModelMapper インスタンス
     */
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
