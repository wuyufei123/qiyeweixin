package com.ctsi.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface ApiService {
	public JSONObject getUserList();

	public JSONObject createChatGroup(JSONObject groupParam);

	public JSONObject sendMsgChatGroup(String chat_id);
}
