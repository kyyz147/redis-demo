package com.example.demo.service;

import java.util.Map;

import com.example.demo.bean.User;

public interface Userservice {
	public String getString(String key);
	public User login(String username,String password);
	/*验证用户账号是否有效*/
	public String loginValdate(String username);
	/*判断账户是否被锁*/
	public Map<String, Object> loginUserLock(String username);
}
