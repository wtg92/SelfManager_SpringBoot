package manager.system.career;

import com.fasterxml.jackson.annotation.JsonFormat;
import manager.exception.NoSuchElement;
import manager.system.VerifyUserMethod;
import manager.util.SystemUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PlanSetting {
	UNDECIDED(0,"",""),
	ALLOW_OTHERS_COPY_PLAN_ITEMS(1,"允许计划项被其它用户复制",
			"当勾选后，非本人的用户可以通过ID复制计划项"),
	;
	
	private int dbCode;
	private String name;
	private String description;
	PlanSetting(int dbCode,String name,String description) {
		this.dbCode = dbCode;
		this.name = name;
		this.description = description;
	}

	public static List<PlanSetting> getSettings(){
		return Arrays.stream(values()).filter(one->one!= PlanSetting.UNDECIDED)
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

	public static PlanSetting valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), PlanSetting.class);
		} catch (NoSuchElement e) {
			assert false : dbCode;
			return PlanSetting.UNDECIDED;
		}
	}
	
}
