package com.example.sample.logging;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Controller / Service のメソッド呼出し共通ログ出力 Aspect。
 * <p>
 * - 対象: com.example.sample.controller..* と com.example.sample.service..* パッケージ配下の全 public メソッド
 * - 目的: 重複したログ記述を抑制し、呼び出しトレースを一元化
 * - 既存の個別ログ（特定処理の詳細）はそのまま併用可能
 */
@Slf4j
@Aspect
@Component
public class InvocationLoggingAspect {

    /** Controller 層対象 Pointcut */
    @Pointcut("execution(public * com.example.sample.controller..*.*(..))")
    public void controllerMethods() {}

    /** Service 層対象 Pointcut */
    @Pointcut("execution(public * com.example.sample.service..*.*(..))")
    public void serviceMethods() {}

    /** Controller + Service 共通 */
    @Pointcut("controllerMethods() || serviceMethods()")
    public void appLayerMethods() {}

    /** メソッド呼出前ログ */
    @Before("appLayerMethods()")
    public void logBefore(JoinPoint jp) {
        if (log.isDebugEnabled()) {
            log.debug("呼出: {}.{} 引数={}",
                jp.getSignature().getDeclaringTypeName(),
                jp.getSignature().getName(),
                Arrays.toString(jp.getArgs()));
        }
    }

    /** メソッド終了ログ（正常終了かどうかは不明） */
    @AfterReturning(pointcut = "appLayerMethods()")
    public void logAfter(JoinPoint jp) {
        if (log.isDebugEnabled()) {
            log.debug("終了: {}.{}", jp.getSignature().getDeclaringTypeName(), jp.getSignature().getName());
        }
    }
}
