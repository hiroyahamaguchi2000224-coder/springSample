package com.example.sample.db.jpa.usercompany;

import java.io.Serializable;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザ所属マスタの複合主キー。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCompanyId implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String userId;
    private String companyId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserCompanyId that = (UserCompanyId) o;
        return Objects.equals(userId, that.userId) && 
               Objects.equals(companyId, that.companyId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, companyId);
    }
}
