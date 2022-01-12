package manager.entity.general.finance;

import java.util.Calendar;

import manager.annotation.Neo4jField;
import manager.entity.general.SMGeneralEntity;

public class Asset extends SMGeneralEntity{

	private static final long serialVersionUID = 1L;
	
	@Neo4jField
	private Double value;
	/**
	 * 值单位 元/美元/比特币/自定义 
	 */
	@Neo4jField
	private String valueUnit;
	/**
	 * 由于汇率等 钱的变动至少是以天为单位的 因此不记录时分秒 无意义
	 */
	@Neo4jField
	private Calendar valueDate;
	/**
	 * 资产/不动产/自定义
	 */
	@Neo4jField
	private String type;
	
	@Neo4jField
	private String remark;
	
	@Neo4jField
	private String name;
	
	/**
	 * 支出/收入
	 */
	@Neo4jField
	private Boolean isExpenses;
	
	
	public Boolean getIsExpenses() {
		return isExpenses;
	}

	public void setIsExpenses(Boolean isExpenses) {
		this.isExpenses = isExpenses;
	}

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getValueUnit() {
		return valueUnit;
	}

	public void setValueUnit(String valueUnit) {
		this.valueUnit = valueUnit;
	}

	public Calendar getValueDate() {
		return valueDate;
	}

	public void setValueDate(Calendar valueDate) {
		this.valueDate = valueDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
