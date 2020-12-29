package com.ctsi.mapper;

import com.ctsi.model.AccessToken;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TokenMapper {
    //获取token
    AccessToken getToken();

    //插入token
    void setToken(AccessToken accessToken);
}
