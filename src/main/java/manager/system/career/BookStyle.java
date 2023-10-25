package manager.system.career;

import com.fasterxml.jackson.annotation.JsonFormat;
import manager.exception.NoSuchElement;
import manager.util.SystemUtil;

/**
 * 对于该枚举，Color 应该放到枚举里，因为这是它独有的，不存在复用的问题。
 * */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BookStyle {
	UNDECIDED(0,"",""),
	YELLOW(1,"#fed754"," #ebb501"),
	CYAN_BLUE(2,"#abc3b5","#7ea38e"),
	BLUE(3,"#2e95aa","#1e606e"),
	RED(4,"#cc4b48"," #9c2e2b"),
	GREEN(5,"#28a745","green"),
	BLACK(6,"rgba(0,0,0,0.8)","#fff"),
	GENDER_RED(7,"#eb6767","#e48383"),
	LIGHT_BLUE(8,"#007bff","#0269d6"),
	
	;
	
	private int dbCode;
	private String mainColor;
	private String subColor;

	private BookStyle(int dbCode, String mainColor, String subColor) {
		this.dbCode = dbCode;
		this.mainColor = mainColor;
		this.subColor = subColor;
	}

	public int getDbCode() {
		return dbCode;
	}

	public void setDbCode(int dbCode) {
		this.dbCode = dbCode;
	}

	public String getMainColor() {
		return mainColor;
	}

	public void setMainColor(String mainColor) {
		this.mainColor = mainColor;
	}

	public String getSubColor() {
		return subColor;
	}

	public void setSubColor(String subColor) {
		this.subColor = subColor;
	}

	public static BookStyle valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), BookStyle.class);
		} catch (NoSuchElement e) {
			assert false : dbCode;
			return BookStyle.UNDECIDED;
		}
	}
	
}
