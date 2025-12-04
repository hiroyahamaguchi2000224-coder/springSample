package com.example.sample.db.mybatis.account;

import org.apache.ibatis.annotations.Mapper;

/**
 * Account テーブル用 MyBatis Mapper。
 */
@Mapper
public interface AccountMapper {
    /**
     * 指定したユーザーIDに対応する Account を取得する。
     *
     * @param userId 検索するユーザーID
     * @return 見つかった場合は AccountMapping、存在しなければ null
     */
    AccountMapping findByUserId(String userId);
}
