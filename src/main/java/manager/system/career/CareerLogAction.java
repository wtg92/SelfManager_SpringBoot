package manager.system.career;

import manager.exception.NoSuchElement;
import manager.util.SystemUtil;

public enum CareerLogAction {
	UNDECIDED(0),
	ADD_ROOT_ITEM_TO_PLAN(1),
	ADD_SON_ITEM_TO_PLAN(2),
	REMOVE_ITEM_FROM_PLAN_AS_FATHER(3),
	REMOVE_ITEM_FROM_PLAN_DUE_TO_FATHER_REMOVED(4),
	UPDATE_ROOT_PLAN_ITEM(5),
	UPDATE_SON_PLAN_ITEM(6),
	PLAN_STATE_CHANGED_BY_DATE(7),
	CREATE_PLAN(8),
	SAVE_PLAN(9),
	STATE_CHENGED_DUE_TO_SAVING_PLAN(10),
	ABANDON_PLAN(11),
	FINISH_PLAN(12),
	WS_STATE_CHANGED_BY_DATE(13),
	OPEN_WS_TODAY(14),
	WS_STATE_CHENGED_DUE_TO_ITEM_MODIFIED(15),
	CREATE_PLAN_DEPT(16),
	SYNC_ITEM_FOR_DEPT(17),
	REMOVE_DEPT_ITEM_DUE_TO_ZERO_VAL(18),
	ADD_DEPT_ITEM(19),
	
	MODIFY_DEPT_ITEM_VAL(20),
	MODIFY_DEPT_ITEM_VAL_AND_NAME(21),
	MODIFY_DEPT_ITEM_NAME_CAUSE_MERGE(22),
	
	CLEAR_DEPT_LOGS_WHEN_TOO_MUCH(23),
	
	COPY_PLAN_ITEMS(24)
	
	;
	
	private int dbCode;
	
	
	private CareerLogAction(int dbCode) {
		this.dbCode = dbCode;
	}


	public int getDbCode() {
		return dbCode;
	}


	public void setDbCode(int dbCode) {
		this.dbCode = dbCode;
	}


	public static CareerLogAction valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), CareerLogAction.class);
		} catch (NoSuchElement e) {
			assert false : dbCode;
			return UNDECIDED;
		}
		
	}
}
