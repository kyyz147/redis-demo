package com.example.demo.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.bean.User;
import com.example.demo.service.Userservice;

@Controller
@RequestMapping("user")
public class UserController {
	@Autowired
	private Userservice userservice;
	/*用户在2分钟内,如果5次密码错误,锁账户一小时*/
	@ResponseBody
	@RequestMapping(produces = {"application/json;charset=UTF-8"},value="login")
	public String login(@RequestParam("username")String username,@RequestParam("password")String password,@RequestParam("valcode")String valcode){
		/*先判断用户是否被锁*/
		Map<String, Object> loginUserLock = userservice.loginUserLock(username);
		if((boolean)loginUserLock.get("result")){
			return "登陆失败,用户被锁,还剩"+loginUserLock.get("locktime")+"分钟";
		}else{
			User login = userservice.login(username, password);
			if(null!=login){
				/*登录成功,清除用户缓存*/
				return "/tiaozhuangyemian.jsp";
			}else{
				/*登陆不成功的话,在缓存里记录一次失败记录*/
				String loginValdate = userservice.loginValdate(username);
				return loginValdate;
			}
		}
	}
}
