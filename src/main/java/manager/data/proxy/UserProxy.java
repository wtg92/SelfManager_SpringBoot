package manager.data.proxy;

import java.util.Set;

import com.alibaba.fastjson2.annotation.JSONField;

import com.fasterxml.jackson.annotation.JsonIgnore;
import manager.entity.general.User;
import manager.system.SelfXPerms;
/**
  *  这个类内部处理前台需要的权限的事，虽然耦合了，但是放在这里似乎最方便
 * @author 王天戈
 *
 */
public class UserProxy{
	public User user;
	
	@JSONField(serialize = false)
	@JsonIgnore
	public Set<SelfXPerms> perms;
	
	public String genderInfo;
	
	
	@JSONField(name="genderInfo")
	public String parseGenderInfo() {
		return user.getGender().getName();
	}
	

	
}
