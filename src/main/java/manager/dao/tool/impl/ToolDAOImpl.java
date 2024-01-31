package manager.dao.tool.impl;

import static manager.util.DBUtil.insertEntity;
import static manager.util.DBUtil.selectAllEntities;
import static manager.util.DBUtil.selectUniqueExistedEntityByField;
import static manager.util.DBUtil.updateExistedEntity;

import java.util.List;

import org.hibernate.SessionFactory;

import manager.dao.tool.ToolDAO;
import manager.entity.general.tool.ToolRecord;
import manager.exception.DBException;
import manager.system.SMDB;
import manager.system.tool.Tool;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
@Repository
public class ToolDAOImpl implements ToolDAO {

	@Resource
	private SessionFactory sessionFactory;

	@Override
	public long insertToolRecord(ToolRecord record) throws DBException {
		return insertEntity(record, sessionFactory);
	}

	@Override
	public void updateExistedToolRecord(ToolRecord record) throws DBException {
		updateExistedEntity(record, sessionFactory);
		
	}

	@Override
	public List<ToolRecord> selectAllToolRecords() throws DBException {
		return selectAllEntities(ToolRecord.class, sessionFactory);
	}

	@Override
	public ToolRecord selectToolRecordByTool(Tool tool) throws DBException {
		return selectUniqueExistedEntityByField(ToolRecord.class, SMDB.F_TOOL, tool, sessionFactory);
	}
	
	
}
