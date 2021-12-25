package manager.system;

import manager.exception.NoSuchElement;
import manager.util.SystemUtil;

public enum TagType {
	UNDECIDED(0),
	PLAN(1),
	WORKSHEET(2),
	;
	private int dbCode;
	private TagType(int dbCode) {
		this.dbCode = dbCode;
	}
	
	public static TagType valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), TagType.class);
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
	
	
}
