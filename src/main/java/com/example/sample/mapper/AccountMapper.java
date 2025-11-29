package com.example.sample.mapper;

import com.example.sample.entity.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {
    /**
     * 指定したユーザーIDに対応する `Account` を取得する MyBatis マッパー。
     *
     * @param userId 検索するユーザーID
     * @return 見つかった場合は `Account`、存在しなければ `null`
     */
    Account findByUserId(String userId);
}
