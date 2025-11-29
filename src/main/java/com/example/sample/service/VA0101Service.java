package com.example.sample.service;

import com.example.sample.exception.ServiceException;
import org.springframework.stereotype.Service;

@Service
public class VA0101Service {
    /**
     * VA0101 用の業務ロジックを提供するサービスクラス。
     */
    
    /**
     * VA0101 の処理を実行する。
     *
     * @param parameter 処理に必要なパラメータ
     * @throws ServiceException パラメータが不正な場合
     */
    public void executeVA(String parameter) {
        if (parameter == null || parameter.isEmpty()) {
            throw new ServiceException("VA001", "パラメータが不正です。");
        }
        
        // VA0101の具体的な処理をここに実装
        System.out.println("VA0101処理実行: " + parameter);
    }
}
