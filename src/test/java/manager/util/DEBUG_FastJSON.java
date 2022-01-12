package manager.util;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.alibaba.fastjson.JSON;

import manager.data.LoginInfo;
import manager.data.proxy.UserProxy;
import manager.entity.general.User;
import manager.system.Gender;

public class DEBUG_FastJSON {
	
	@Test
	public void test1() {
		String account = "wwwwww";
		String email = "email@126.com";
		String pwd= "complacte1!!!!!!!!!";
		String nickName="lkkk";
		Gender gender = Gender.MALE;
		User user = new User();
		user.setId((long)5);
		user.setAccount(account);
		user.setEmail(email);
		user.setPassword(pwd);
		user.setNickName(nickName);
		user.setGender(gender);
		String userStr = JSON.toJSONString(user);
		User user2 =JSON.parseObject(userStr, User.class);
		assertEquals(gender, user2.getGender());
		UserProxy proxy = new UserProxy();
		proxy.user = user;
		System.out.println(JSON.toJSONString(proxy));
	}
	
	@Test
	public void testLogger() {
		LoginInfo info = new LoginInfo(new UserProxy());
		info.success = false;
		System.out.println(JSON.toJSONString(info));
	}
	
	@Test
	public void testMap() {
		Map<String,Integer> map = new HashMap<>();
		map.put("1", 111);
		map.put("3", 500);
		System.out.println(JSON.toJSONString(map));
	}
		
}
