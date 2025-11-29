package com.example.sample.mapper;

import com.example.sample.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MenuMapper {
    /**
     * メニューを表示順で取得する MyBatis マッパー。
     */
    List<Menu> findAllOrdered();
}
