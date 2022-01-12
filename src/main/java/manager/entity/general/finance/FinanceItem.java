package manager.entity.general.finance;

import manager.entity.general.SMGeneralEntity;

public class FinanceItem extends SMGeneralEntity{

	private static final long serialVersionUID = 1L;

	private String name;
	
	private String remark;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	
}
