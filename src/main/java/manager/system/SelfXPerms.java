package manager.system;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import manager.exception.NoSuchElement;
import manager.util.SystemUtil;


public enum SelfXPerms {
	UNDECIDED(0,""),
	
	ADD_USERS_TO_PERM(1,"给用户分组"),
	EDIT_PERMS_TO_GROUP(2,"编辑用户组权限"),
	CREATE_USER_GROUP(3,"创建用户组"),
	SEE_USERS_AND_USER_GROUPS_DATA(4,"看到用户/用户组相关统计数据"),
	SEE_USERS_MODULE(5,"看到用户管理模块"),
	CREATE_WORKSHEET_PLAN(6,"可以创建工作表计划"),
	SEE_SELF_PLANS(7,"看到自己的计划"),
	SEE_TODAY_WS_COUNT(8,"可以看到今日工作表统计"),
	SEE_BOOKS_MODULE(9,"看到书库模块"),
	CREATE_NOTE_BOOK_AND_NOTE(10,"创建笔记本和笔记"),
	SEE_WORKSHEET_MODULE(11,"看到工作表模块"),
	SEE_TOOLS_MODULE(12,"看到工具模块"),

	DO_MIGRATION_OF_VER1(13,"可以触发数据迁移"),


	;
    private final int dbCode;
	private final String name;
    
	public static SelfXPerms valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), SelfXPerms.class);
		} catch (NoSuchElement e) {
			assert false : dbCode;
			return UNDECIDED;
		}
	}
	
	private SelfXPerms(int dbCode, String name) {
		this.name = name;
		this.dbCode = dbCode;
	}
	public String getName() {
		return name;
	}
	public int getDbCode() {
		return dbCode;
	}
    
    public static Map<String,List<SelfXPerms>> getPermsByGroup(){
    	Map<String,List<SelfXPerms>> rlt = new HashMap<>();
		//SEE_USERS_MODULE 用户管理模块不允许配置
    	rlt.put("模块可见性",Arrays.asList(SEE_BOOKS_MODULE,SEE_TOOLS_MODULE,SEE_WORKSHEET_MODULE));
    	rlt.put("用户相关", Arrays.asList(ADD_USERS_TO_PERM,EDIT_PERMS_TO_GROUP,CREATE_USER_GROUP, SEE_USERS_AND_USER_GROUPS_DATA));
    	rlt.put("工作表相关",Arrays.asList(CREATE_WORKSHEET_PLAN,SEE_SELF_PLANS,SEE_TODAY_WS_COUNT));
    	rlt.put("笔记相关",Arrays.asList(CREATE_NOTE_BOOK_AND_NOTE));
    	
    	assert rlt.values().stream().mapToInt(elements->elements.size()).sum()  == values().length - 1 : "有权限未配置进组内";
    	return rlt;
    }
}
