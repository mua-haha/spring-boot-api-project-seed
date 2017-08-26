package com.conpany.project;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.company.project.model.User;
import com.company.project.service.UserService;

import tk.mybatis.mapper.entity.Condition;


public class TestService extends Tester{
	
	@Resource
	private UserService userService;
	
	@Test
	public void  test(){
		User user = new User();
		user.setRoleId(2);
		List<User> list = userService.getBeanList(user);
		Condition con = new Condition(User.class);
//		con.or().andAllEqualTo(param)
		System.out.println(list.get(0).getRealname());
		
		
	}
	public static void main(String[] args) {
		List<String> addLines = new ArrayList<>();
		List<String> mapperLines = new ArrayList<>();
		addLines.add("1");
		addLines.add("2");
		addLines.add("3");
		mapperLines.add("a");
		mapperLines.add("b");
		mapperLines.add("c");
		mapperLines.addAll(mapperLines.size()-1, addLines);
		for(String s:mapperLines){
			System.out.println(s);
		}
	}
	
	

}
