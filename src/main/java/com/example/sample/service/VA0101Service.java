package com.example.sample.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.sample.db.jpa.cart.CartEntity;
import com.example.sample.db.jpa.cart.CartRepository;
import com.example.sample.db.jpa.product.ProductEntity;
import com.example.sample.db.jpa.product.ProductRepository;
import com.example.sample.exception.ServiceException;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * VA0101 商品検索 Service。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class VA0101Service {
    
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final MessageSource messageSource;
    
    /**
     * 商品検索結果 DTO（Service 内部クラス）。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSearchResultDto {
        /** 商品ID */
        private String productId;
        /** 商品名 */
        private String productName;
        /** 販売会社ID */
        private String companyId;
        /** 価格 */
        private BigDecimal price;
        /** 在庫数 */
        private Integer stockQuantity;
        /** 商品説明 */
        private String description;
    }
    
    /**
     * ユーザの所属会社が購入可能な商品を検索する。
     *
     * @param companyId ユーザの所属会社ID
     * @param productName 商品名（部分一致、空文字の場合はnull扱い）
     * @return 商品検索結果DTOのリスト
     */
    @Transactional(readOnly = true)
    public List<ProductSearchResultDto> searchProducts(String companyId, String productName) {
        log.debug("searchProducts() 呼出: companyId={}, productName={}", 
            companyId, productName);
        
        // 空文字をnullに変換
        String pName = (productName != null && !productName.trim().isEmpty()) ? productName.trim() : null;
        
        // 商品検索
        List<ProductEntity> products = productRepository.findAvailableProducts(companyId, pName);
        log.debug("検索結果: {} 件", products.size());
        
        // DTOに変換
        return products.stream()
            .map(this::convertToDto)
            .toList();
    }
    
    /**
     * 商品をカートに追加する。
     *
     * @param userId ユーザID
     * @param productIds 商品IDの配列
     * @throws ServiceException 商品が見つからない場合
     */
    @Transactional
    public void addToCart(String userId, String[] productIds) {
        log.debug("addToCart() 呼出: userId={}, productIds={}", userId, productIds);
        
        if (productIds == null || productIds.length == 0) {
            String message = messageSource.getMessage("E0202", null, null);
            throw new ServiceException("E0202", message);
        }
        
        for (String productId : productIds) {
            // 既にカートに存在するか確認
            var existingCart = cartRepository.findByUserIdAndProductId(userId, productId);
            
            if (existingCart.isPresent()) {
                // 既存の場合は数量を+1
                CartEntity cart = existingCart.get();
                cart.setQuantity(cart.getQuantity() + 1);
                cart.setUpdatedAt(LocalDateTime.now());
                cartRepository.save(cart);
                log.debug("カート更新: productId={}, quantity={}", productId, cart.getQuantity());
            } else {
                // 新規追加
                CartEntity cart = new CartEntity();
                cart.setUserId(userId);
                cart.setProductId(productId);
                cart.setQuantity(1);
                cart.setDelFlg(false);
                cart.setCreatedAt(LocalDateTime.now());
                cart.setUpdatedAt(LocalDateTime.now());
                cartRepository.save(cart);
                log.debug("カート追加: productId={}", productId);
            }
        }
        
        log.info("カート追加完了: userId={}, 追加件数={}", userId, productIds.length);
    }
    
    /**
     * ProductEntity を DTO に変換する。
     */
    private ProductSearchResultDto convertToDto(ProductEntity product) {
        return new ProductSearchResultDto(
            product.getProductId(),
            product.getProductName(),
            product.getCompanyId(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getDescription()
        );
    }
}
