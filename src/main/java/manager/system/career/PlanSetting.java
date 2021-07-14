package manager.system.career;

import manager.exception.NoSuchElement;
import manager.util.SystemUtil;

public enum PlanSetting {
	UNDECIDED(0,""),
	ALLOW_OTHERS_COPY_PLAN_ITEMS(1,"允许计划项被其它用户复制"),
	
	;
	
	private int dbCode;
	private String name;
	
	private PlanSetting(int dbCode,String name) {
		this.dbCode = dbCode;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDbCode() {
		return dbCode;
	}

	public void setDbCode(int dbCode) {
		this.dbCode = dbCode;
	}

	public static PlanSetting valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), PlanSetting.class);
		} catch (NoSuchElement e) {
			assert false : dbCode;
			return PlanSetting.UNDECIDED;
		}
	}
	
}
