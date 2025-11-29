package com.example.sample.service;

import com.example.sample.exception.ServiceException;
import org.springframework.stereotype.Service;

@Service
public class VB0101Service {
    /**
     * VB0101 用の業務ロジックを提供するサービスクラス。
     */
    
    /**
     * VB0101 の処理を実行する。
     *
     * @param parameter 処理に必要なパラメータ
     * @throws ServiceException パラメータが不正な場合
     */
    public void executeVB(String parameter) {
        if (parameter == null || parameter.isEmpty()) {
            throw new ServiceException("VB001", "パラメータが不正です。");
        }
        
        // VB0101の具体的な処理をここに実装
        System.out.println("VB0101処理実行: " + parameter);
    }
}
