package manager.system.career;

import com.fasterxml.jackson.annotation.JsonFormat;
import manager.exception.NoSuchElement;
import manager.util.SystemUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PlanItemType {
	UNDECIDED(0,""),
	MINUTES(1,"分钟"),
	TIMES(2,"次"),
	;
	
	private int dbCode;
	private String name;
	
	PlanItemType(int dbCode,String name) {
		this.dbCode = dbCode;
		this.name = name;
	}

    public static List<PlanItemType> getTypes() {
		return Arrays.stream(values()).filter(one->one!= UNDECIDED)
				.collect(Collectors.toList());
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


	public static PlanItemType valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), PlanItemType.class);
		} catch (NoSuchElement e) {
			assert false : dbCode;
			return UNDECIDED;
		}
		
	}
	
	public static PlanItemType valueOfDBCode(String dbCode) {
		return valueOfDBCode(Integer.parseInt(dbCode));
	}
}
