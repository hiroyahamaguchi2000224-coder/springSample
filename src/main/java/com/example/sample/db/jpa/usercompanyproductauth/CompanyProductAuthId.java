package com.example.sample.db.jpa.usercompanyproductauth;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会社別購入可能商品承認マスタの複合主キー。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyProductAuthId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String companyId;
    private String salesCompanyId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompanyProductAuthId that = (CompanyProductAuthId) o;
        return Objects.equals(companyId, that.companyId) && 
               Objects.equals(salesCompanyId, that.salesCompanyId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(companyId, salesCompanyId);
    }
}
