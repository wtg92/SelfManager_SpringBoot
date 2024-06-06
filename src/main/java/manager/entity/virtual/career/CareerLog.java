package manager.entity.virtual.career;

import java.util.Calendar;
import java.util.LinkedList;

import com.alibaba.fastjson2.annotation.JSONField;

import manager.entity.virtual.SMVirtualEntity;
import manager.system.career.CareerLogAction;

public class CareerLog extends SMVirtualEntity{
	
	@JSONField(serialize = false)
	private CareerLogAction action;
	@JSONField(serialize = false)
	private LinkedList<String> params = new LinkedList<String>();
	@JSONField(serialize = false)
	private Long creatorId ;

	@Deprecated
	private Calendar createTime;

	private Long createUTC;

	public Long getCreateUTC() {
		return createUTC;
	}

	public void setCreateUTC(Long createUTC) {
		this.createUTC = createUTC;
	}

	public Calendar getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Calendar createTime) {
		this.createTime = createTime;
	}

	public void addParams(Object ...value) {
		for(Object val:value) {
			params.add(val.toString());
		}
	}

	public String pollParam() {
		return params.poll();
	}
	
	public String getParam(int index) {
		return params.get(index);
	}
	
	public CareerLog(CareerLogAction action, long creatorId) {
		super();
		this.action = action;
		this.creatorId = creatorId;
	}

	public LinkedList<String> getParams() {
		return params;
	}

	public void setParams(LinkedList<String> params) {
		this.params = params;
	}

	public CareerLogAction getAction() {
		return action;
	}

	public void setAction(CareerLogAction action) {
		this.action = action;
	}

	public Long getCreatorId() {
		return creatorId;
	}

	public void setCreatorId(Long creatorId) {
		this.creatorId = creatorId;
	}
	
	
	
}
