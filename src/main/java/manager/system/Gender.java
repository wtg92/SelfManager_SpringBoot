package manager.system;

import manager.exception.NoSuchElement;
import manager.util.SystemUtil;

public enum Gender {
	UNDECIDED(0,""),
	MALE(1,"男"),
	FEMALE(2,"女"),
	OTHERS(3,"其它"),
	UNKNOWN(4,"保密")
	;
	private int dbCode;
	private String name;
	private Gender(int dbCode, String name) {
		this.dbCode = dbCode;
		this.name = name;
	}
	
	public static Gender valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), Gender.class);
		} catch (NoSuchElement e) {
			assert false : dbCode;
			return UNDECIDED;
		}
		
	}

	public int getDbCode() {
		return dbCode;
	}

	public void setDbCode(int dbCode) {
		this.dbCode = dbCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
