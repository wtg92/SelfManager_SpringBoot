package manager.data.tool;

import java.util.ArrayList;
import java.util.List;

import manager.data.proxy.tool.ToolRecordProxy;

/**
  *  给redis 缓存用的，因为功能反正都是要加载出所有的Record  放在redis中
 * @author 王天戈
 *
 */
public class ToolRecordSummary {
	public List<ToolRecordProxy> records = new ArrayList<>();

	public ToolRecordSummary(List<ToolRecordProxy> records) {
		super();
		this.records = records;
	}
	
	
}
