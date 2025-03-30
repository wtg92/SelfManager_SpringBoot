package manager.data.proxy.career;

import manager.entity.virtual.worksheet.CareerLog;

import java.util.List;

public class CareerLogProxy {
	
	public CareerLog log;
	public int code;

	public List<String> params;
	public String creatorName;
	public boolean isBySystem;


	public CareerLogProxy(CareerLog log) {
		super();
		this.log = log;
	}

}
