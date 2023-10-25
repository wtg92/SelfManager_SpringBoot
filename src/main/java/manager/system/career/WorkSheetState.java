package manager.system.career;

import com.fasterxml.jackson.annotation.JsonFormat;
import manager.exception.NoSuchElement;
import manager.util.ColorUtil;
import manager.util.SystemUtil;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum WorkSheetState {
	UNDECIDED(0,""),
	ACTIVE(1,"进行中"),
	FINISHED(2,"完成"),
	NO_MONITOR(3,"不监控"),
	OVERDUE(4,"超期"),
	OVER_FINISHED(5,"超额完成")
	;
	
	private int dbCode;
	private String name;
	
	private WorkSheetState(int dbCode,String name) {
		this.dbCode = dbCode;
		this.name = name;
	}

	public String getColor() {
		return ColorUtil.getColor(this.getName());
	}
	
	public int getDbCode() {
		return dbCode;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDbCode(int dbCode) {
		this.dbCode = dbCode;
	}


	public static WorkSheetState valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), WorkSheetState.class);
		} catch (NoSuchElement e) {
			assert false : dbCode;
			return UNDECIDED;
		}
		
	}
	
	public static WorkSheetState valueOfDBCode(String dbCode) {
		return valueOfDBCode(Integer.parseInt(dbCode));
	}
}
