package manager.entity.virtual.career;

import manager.entity.virtual.SMVirtualEntity;
import manager.system.career.PlanItemType;

public class PlanDeptItem extends SMVirtualEntity {
	
	private String name;
	
	/*表明分钟/次数*/
	private PlanItemType type;
	
	private Double value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlanItemType getType() {
		return type;
	}

	public void setType(PlanItemType type) {
		this.type = type;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

		
}

