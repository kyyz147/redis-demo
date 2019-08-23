package com.example.demo.serviceimpl;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.example.demo.bean.User;
import com.example.demo.service.Userservice;
@Service
public class Userserviceimpl implements Userservice{
	@Autowired
	RedisTemplate<String, String> redistemplate;
	@Override
	public String getString(String key) {
		ValueOperations<String, String> opsForValue = redistemplate.opsForValue();
		redistemplate.opsForValue().set("test2", "测试时间", 2, TimeUnit.MINUTES);
		
		if(redistemplate.hasKey(key)){
			//如果resid里有就去除返回
			System.out.println("redis中取出---------"+new Date());
			return opsForValue.get(key);
		}else{
			opsForValue.set(key, "往redis中存入并返回");
			System.out.println("往redis中存入并返回");
			return "往redis中存入并返回";
		}
		
	}
	@Override
	public User login(String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}
	/*登陆失败的操作*/
	@Override
	public String loginValdate(String username) {
		int num=5;
		/*记录登陆错误次数key*/
		String key=User.getLoginCountFailKey(username);
		if(!redistemplate.hasKey(key)){
			/*如果redis中不存在,redis中存入一个key,value为1*/
			redistemplate.opsForValue().set(key, "1");
			/*设置失效期2分钟*/
			redistemplate.expire(key, 2, TimeUnit.MINUTES);
			return "登陆失败,2分钟内 还可以登陆"+(num-1)+"次";
		}else{
			/*redis中存在的话,取出value*/
			long count=Long.parseLong(redistemplate.opsForValue().get(key));
			/*判断失败的次数*/
			if(count<(num-1)){
				/*如果登陆失败的次数不到5次,还可以继续登陆*/
				/*对指定的key增加value的值*/
				redistemplate.opsForValue().increment(key, 1);
				/*取key的有效时间还剩多少秒*/
				Long expire = redistemplate.getExpire(key,TimeUnit.SECONDS);
				return "登陆失败,在"+expire+"秒内,还允许输入错误"+(num-1-count)+"次";
			}else{
				/*超过限制的登陆次数,锁定用户*/
				redistemplate.opsForValue().set(User.getLoginTimeLockKey(username), "1");
				/*锁定用户*/
				redistemplate.expire(User.getLoginTimeLockKey(username),1,TimeUnit.HOURS);
				return "因登陆失败次数超过限制"+num+"次,已对其限制登陆1小时";
			}
		}
	}
	/*判断用户是否被限制,查询当前key是否存在,存在的话就是被限制*/
	@Override
	public Map<String, Object> loginUserLock(String username) {
		String string=User.getLoginTimeLockKey(username);
		Map<String, Object> map=new HashMap<String, Object>();
		if(redistemplate.hasKey(string)){
			Long expire = redistemplate.getExpire(string, TimeUnit.MINUTES);
			/*如果存在*/
			map.put("result", true);
			/*还剩多长时间解锁*/
			map.put("locktime", expire);
		}else{
			map.put("result", false);
		}
		return map;
	}
	
}
