package com.ctsi.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ctsi.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class apiController {

	@Autowired
	private ApiService apiService;

	@GetMapping("/test")
	public String test() {
		return "HelloWorld";
	}

	//列出用户
	@GetMapping("/userlist")
	public JSONObject userList() {
		JSONObject result = apiService.getUserList();
//		System.out.println(result);
		return result;
	}

	//根据所选用户创建群聊
	@PostMapping("/createChat")
	public JSONObject createChat(@RequestBody JSONObject groupParam) {
//		List<String> user_list = new ArrayList<>(); //["cheng","chen","sang"]
		JSONObject result = apiService.createChatGroup(groupParam);
		return result;
	}
}
