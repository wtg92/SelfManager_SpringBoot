package manager.system.career;

import com.fasterxml.jackson.annotation.JsonFormat;
import manager.exception.NoSuchElement;
import manager.util.ColorUtil;
import manager.util.SystemUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PlanState {
	UNDECIDED(0,""),
	ACTIVE(1,"进行中"),
	ABANDONED(2,"废弃"),
	FINISHED(3,"完成"),
	PREPARED(4,"预备"),
	;
	
	private int dbCode;
	private String name;
	
	PlanState(int dbCode,String name) {
		this.dbCode = dbCode;
		this.name = name;
	}

    public static List<PlanState> getStates() {
		return Arrays.stream(values()).filter(one->one!= UNDECIDED)
				.collect(Collectors.toList());
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


	public static PlanState valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), PlanState.class);
		} catch (NoSuchElement e) {
			assert false : dbCode;
			return UNDECIDED;
		}
		
	}
	
	public static PlanState valueOfDBCode(String dbCode) {
		return valueOfDBCode(Integer.parseInt(dbCode));
	}
}
