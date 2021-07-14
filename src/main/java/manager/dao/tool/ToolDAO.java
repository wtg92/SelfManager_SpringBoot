package manager.dao.tool;

import java.util.List;

import manager.entity.general.tool.ToolRecord;
import manager.exception.DBException;
import manager.system.tool.Tool;

public interface ToolDAO {
	
	int insertToolRecord(ToolRecord record) throws DBException;
	
	ToolRecord selectToolRecordByTool(Tool tool) throws DBException;
	
	void updateExistedToolRecord(ToolRecord record) throws DBException;
	
	List<ToolRecord> selectAllToolRecords() throws DBException;
}
