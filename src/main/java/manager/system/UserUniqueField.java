package manager.system;

import manager.exception.NoSuchElement;
import manager.util.SystemUtil;

public enum UserUniqueField {
	UNDECIDED(0),
	ACCOUNT(1),
	EMAIL(2),
	WEI_XIN_OPEN_ID(3),
	ID_NUM(4),
	TEL_NUM(5),
	NICK_NAME(6)
	;
	private int dbCode;
	private UserUniqueField(int dbCode) {
		this.dbCode = dbCode;
	}
	
	public static UserUniqueField valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), UserUniqueField.class);
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
