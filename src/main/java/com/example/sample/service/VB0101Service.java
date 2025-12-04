package com.example.sample.service;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.example.sample.exception.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class VB0101Service {
    private final MessageSource messageSource;
    
    /**
     * VB0101 用の業務ロジックを提供する Service クラス。
     */
    
    /**
     * VB0101 の処理を実行する。
     *
     * @param parameter 処理に必要なパラメータ
     * @throws ServiceException パラメータが不正な場合
     */
    public void executeVB(String parameter) {
        if (parameter == null || parameter.isEmpty()) {
            String message = messageSource.getMessage("E_VB0101_001", null, null);
            throw new ServiceException("E_VB0101_001", message);
        }
        
        // VB0101の具体的な処理をここに実装
        log.info("VB0101処理実行: {}", parameter);
    }
}
