package com.ctsi.cron;

import com.ctsi.mapper.TokenMapper;
import com.ctsi.model.AccessToken;
import com.ctsi.util.HttpClientHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableScheduling
public class AccessTokenCron {
    public static final Logger logger = LoggerFactory.getLogger(HttpClientHelper.class);
    //获取token接口
    public static final String GETTOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ww353786818bbff142&corpsecret=zjztC61z33Xi-IpTAvb9SjFk0IkMs5KqMuqqt7vYd68";
    @Autowired
    TokenMapper tokenMapper;
    @Autowired
    HttpClientHelper httpClientHelper;

    @Scheduled(cron = "5 * * * * ? ")
    public void taskToken() {
        Map<String, String> param = new HashMap<>();
        //读库
        AccessToken accessToken = tokenMapper.getToken();
        //获取token
        try {
            accessToken.setToken(httpClientHelper.get(GETTOKEN, param).split("\"access_token\":\"")[1].split("\",\"expires_in\"")[0].trim());
        } catch (IOException e) {
            logger.info(String.valueOf(e));
            logger.error("请求token异常");
        }
        accessToken.setTime(new Timestamp(System.currentTimeMillis()));
        //定时更新库
        tokenMapper.setToken(accessToken);
    }

}
