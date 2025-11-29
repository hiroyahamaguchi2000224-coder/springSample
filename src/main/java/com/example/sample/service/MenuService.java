package com.example.sample.service;

import com.example.sample.entity.Menu;
import com.example.sample.mapper.MenuMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuService {
    /**
     * メニューに関する業務ロジックを提供するサービスクラス。
     */
    
    /**
     * メニュー情報 DTO（inner class）。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuDTO {
        /** メニュー項目の識別子 */
        private Long id;
        /** 画面識別子 */
        private String screenId;
        /** 画面名（表示名） */
        private String screenName;
        /** ボタン名（メニューに表示するラベル） */
        private String buttonName;
        /** 表示順序 */
        private Integer displayOrder;
    }
    
    private final MenuMapper menuMapper;
    
    /**
     * 全メニューを取得しる。
     *
     * @return メニュー一覧（表示順でソートされた `List<MenuDTO>`）
     */
    @Transactional(readOnly = true)
    public List<MenuService.MenuDTO> getAllMenus() {
        return menuMapper.findAllOrdered().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * エンティティ Menu を DTO に変換する
     */
    private MenuService.MenuDTO convertToDTO(Menu menu) {
        return new MenuDTO(
            menu.getId(),
            menu.getScreenId(),
            menu.getScreenName(),
            menu.getButtonName(),
            menu.getDisplayOrder()
        );
    }
}
