package manager.dao.tool.impl;

import static manager.util.DBUtil.getHibernateSessionFactory;
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

public class ToolDAOImpl implements ToolDAO {

	private final SessionFactory hbFactory = getHibernateSessionFactory();

	@Override
	public long insertToolRecord(ToolRecord record) throws DBException {
		return insertEntity(record, hbFactory);
	}

	@Override
	public void updateExistedToolRecord(ToolRecord record) throws DBException {
		updateExistedEntity(record, hbFactory);
		
	}

	@Override
	public List<ToolRecord> selectAllToolRecords() throws DBException {
		return selectAllEntities(ToolRecord.class, hbFactory);
	}

	@Override
	public ToolRecord selectToolRecordByTool(Tool tool) throws DBException {
		return selectUniqueExistedEntityByField(ToolRecord.class, SMDB.F_TOOL, tool, hbFactory);
	}
	
	
}
