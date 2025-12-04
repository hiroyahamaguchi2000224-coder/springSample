package com.example.sample.db.jpa.cart;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * カートマスタの複合主キー。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String userId;
    private String productId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartId cartId = (CartId) o;
        return Objects.equals(userId, cartId.userId) && 
               Objects.equals(productId, cartId.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, productId);
    }
}
