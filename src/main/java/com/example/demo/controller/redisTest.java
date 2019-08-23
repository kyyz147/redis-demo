package com.example.demo.controller;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.demo.service.Userservice;
@RunWith(SpringRunner.class)
@SpringBootTest
public class redisTest {
	@Autowired
	private Userservice userservice;
	
	
	@Test
	public void test1(){
		String string = userservice.getString("汉字");
		System.out.println(string+new Date());
	}
}
