package manager.system.career;

import com.fasterxml.jackson.annotation.JsonFormat;
import manager.exception.NoSuchElement;
import manager.util.SystemUtil;
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WorkItemType {
	UNDECIDED(0),
	GENERAL(1),
	/**
	 * startTime=endTime=同步历史欠账的时间。
	 * note=""
	 */
	DEBT(2),
	;
	
	private int dbCode;
	
	private WorkItemType(int dbCode) {
		this.dbCode = dbCode;
	}

	public int getDbCode() {
		return dbCode;
	}

	public void setDbCode(int dbCode) {
		this.dbCode = dbCode;
	}

	public static WorkItemType valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), WorkItemType.class);
		} catch (NoSuchElement e) {
			assert false : dbCode;
			return UNDECIDED;
		}
		
	}
	
	public static WorkItemType valueOfDBCode(String dbCode) {
		return valueOfDBCode(Integer.parseInt(dbCode));
	}
}
