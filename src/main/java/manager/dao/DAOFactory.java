package manager.dao;

import manager.dao.career.NoteDAO;
import manager.dao.career.WorkDAO;
import manager.dao.career.impl.WorkDAOImpl;
import manager.dao.finance.FinanceDAO;
import manager.dao.finance.impl.FinanceDAOImpl;
import manager.dao.impl.UserDAOImpl;
import manager.dao.tool.ToolDAO;
import manager.dao.tool.impl.ToolDAOImpl;
import manager.system.DBConstants;

public abstract class DAOFactory {
	public static void deleteAllTables() {
		for(String table: DBConstants.ALL_TABLES) {
			deleteTable(table);
		}
	}
	
	public static void deleteTable(String tableName) {
		throw new RuntimeException("Haven't finished yet");
	}
}
