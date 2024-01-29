package manager.system;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import manager.exception.NoSuchElement;
import manager.util.SystemUtil;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum VerifyUserMethod {
	UNDECIDED(0,""),
	ACCOUNT_PWD(1,"账号"),
	EMAIL_VERIFY_CODE(2,"邮箱"),
	TEL_VERIFY_CODE(3,"手机")
	;
	private int dbCode;
	private String name;

	private VerifyUserMethod(int dbCode, String name) {
		this.dbCode = dbCode;
		this.name = name;
	}
	
	public static VerifyUserMethod valueOfDBCode(int dbCode) {
		try {
			return SystemUtil.valueOfDBCode(dbCode,e->e.getDbCode(), VerifyUserMethod.class);
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
