package manager.system;

import com.fasterxml.jackson.annotation.JsonFormat;
import manager.exception.NoSuchElement;
import manager.util.SystemUtil;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Gender {
	UNDECIDED(0,"",-99),
	MALE(1,"男",1),
	FEMALE(2,"女",2),
	OTHERS(3,"其它",4),
	UNKNOWN(4,"保密",5),
	TRANS(5,"跨性别者",3)
	;
	private int dbCode;
	private String name;
	private int order;
	private Gender(int dbCode, String name,int order) {
		this.dbCode = dbCode;
		this.name = name;
		this.order = order;
	}

	public static List<Gender> getGenders(){
		return Arrays
				.stream(Gender.values())
				.filter(one->one!=Gender.UNDECIDED)
				.sorted(Comparator.comparing(one->one.order))
				.collect(Collectors.toList());
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
