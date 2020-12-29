package com.ctsi.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ctsi.constant.WeChatApiConstant;
import com.ctsi.mapper.TokenMapper;
import com.ctsi.service.ApiService;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

@Service
public class ApiServiceImpl implements ApiService {
    private final Logger log = LoggerFactory.getLogger(ApiServiceImpl.class);
    @Autowired
    TokenMapper tokenMapper;

    /**
     * @Description 获取用户列表
     * @Date 2020/12/28 15:11
     * @Param * @param
     * @Return com.alibaba.fastjson.JSONObject
     * @Exception
     */
    @Override
    public JSONObject getUserList() {
        String result = "";
        BufferedReader in = null;
//		String url = "https://qyapi.weixin.qq.com/cgi-bin/user/simplelist";
//		int department_id = 1;
//		int fetch_child = 1;
        try {
//			URL realUrl = new URL(appendString(url, access_token, department_id, fetch_child));
            //获取企业内部所有用户信息
            URL realUrl = new URL(WeChatApiConstant.USERLISTSTART + tokenMapper.getToken().getToken() + WeChatApiConstant.USERLISTEND);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
//			Map<String, List<String>> map = connection.getHeaderFields();
//			// 遍历所有的响应头字段
//			System.out.println(map);
//			for (String key : map.keySet()) {
//				System.out.println(key + "--->" + map.get(key));
//			}
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            log.info("获取用户列表，发送GET请求出现异常！" + e);
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        JSONObject jsonObject = JSONObject.parseObject(result);

        if (jsonObject.get("errcode").equals(0)) {
            log.info("请求用户列表成功，返回结果：" + jsonObject);
        } else {
            log.info("请求用户列表失败，错误原因：" + jsonObject.get("errmsg"));
        }
//		JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("userlist").toString());
//		log.info(jsonArray.toString());
//		return jsonArray;
        return jsonObject;
    }

    @Override
    public JSONObject createChatGroup(JSONObject groupParam) {
        JSONObject param = new JSONObject();
        param.put("name", groupParam.get("groupName"));
        param.put("owner", groupParam.get("owner"));
        param.put("userlist", groupParam.get("userlist"));
        log.info("创建群聊参数：" + param);

        JSONObject result = new JSONObject();
        HttpPost post = new HttpPost(WeChatApiConstant.CREATEGROUP + tokenMapper.getToken().getToken());
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            post.setHeader("Content-Type", "application/json;charset=utf-8");
//			post.addHeader("Authorization", "Basic YWRtaW46");
            StringEntity postingString = new StringEntity(param.toString(), "utf-8");
            post.setEntity(postingString);
            HttpResponse response = httpClient.execute(post);

            InputStream in = response.getEntity().getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder strber = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                strber.append(line).append('\n');
            }
            br.close();
            in.close();
            result = JSONObject.parseObject(strber.toString());
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.info("创建群聊请求，服务器异常");
            }
        } catch (Exception e) {
            log.info("创建群聊，请求异常");
            throw new RuntimeException(e);
        } finally {
            post.abort();
        }
        log.info("创建群聊，返回结果：" + result);
        if (result.get("errcode").equals(0)) {
            JSONObject sendMsg = sendMsgChatGroup(result.get("chatid").toString());
            result.put("sendMsg", sendMsg.get("errcode"));
        }
        return result;
    }

    //	@Override
    public JSONObject sendMsgChatGroup(String chat_id) {

        JSONObject param = new JSONObject();
        param.put("chatid", chat_id);
        param.put("msgtype", "text");
        JSONObject text = new JSONObject();
        text.put("content", "您已加入群聊，开始聊天吧~");
        param.put("text", text);

        JSONObject result = new JSONObject();
        HttpPost post = new HttpPost(WeChatApiConstant.GROUPTALK + tokenMapper.getToken().getToken());
        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            post.setHeader("Content-Type", "application/json;charset=utf-8");
            StringEntity postingString = new StringEntity(param.toString(), "utf-8");
            post.setEntity(postingString);
            HttpResponse response = httpClient.execute(post);

            InputStream in = response.getEntity().getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder strber = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                strber.append(line).append('\n');
            }
            br.close();
            in.close();
            result = JSONObject.parseObject(strber.toString());
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                log.info("向群聊" + chat_id + "发送初始消息，服务器异常");
            }
        } catch (Exception e) {
            log.info("向群聊" + chat_id + "发送初始消息，请求异常");
            throw new RuntimeException(e);
        } finally {
            post.abort();
        }
        log.info("向群聊" + chat_id + "发送初始消息，返回结果：" + result);
        return result;
    }

    /**
     * 把请求参数通过?拼接
     * 示例: https://apiurl?access_token=ACCESS_TOKEN&department_id=DEPARTMENT_ID&fetch_child=FETCH_CHILD
     */
    public String appendString(String url, String asccess_token, int department_id, int fetch_child) {
        String str = url +
                "?" +
                "access_token=" +
                asccess_token +
                "&" +
                "department_id=" +
                department_id +
                "&" +
                "fetch_child=" +
                fetch_child;
        return str;
    }
}
