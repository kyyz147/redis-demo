package com.example.demo.bean;

import java.io.Serializable;

import lombok.Data;


@Data
public class User implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6613267330373944467L;
	private String id;
	private String username;
	private String password;
	private String name;
	private String age;
	
	public static String getKeyName(){
		return "user:";
	}
	
	/*存在redis中的key,判断是否锁定*/
	public static String getLoginTimeLockKey(String username){
		return "user:loginname:lock:"+username;
	}
	/*存在redis中的key,记录失败的的次数*/
	public static String getLoginCountFailKey(String username){
		return "user:logincount:fail:"+username;
	}
}
