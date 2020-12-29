package com.ctsi.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;


/**
 * @author: wuyufei
 * @Date: 2020/12/29 10:50
 * @Description: access_token
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccessToken {
    private String token;
    private Timestamp time;
    //企业id
    private String corpId;
    //应用密钥
    private String corpSecret;

}
