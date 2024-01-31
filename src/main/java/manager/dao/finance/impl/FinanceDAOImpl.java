package manager.dao.finance.impl;

import java.util.logging.Logger;


import manager.dao.finance.FinanceDAO;
import manager.exception.DBException;

public class FinanceDAOImpl implements FinanceDAO {

	
	private final static Logger logger = Logger.getLogger("Finance Interface");
	
	private static FinanceDAO instance;
	
	private FinanceDAOImpl() {}
	
	public synchronized static FinanceDAO getInstance() {
		if(instance == null) {
			instance = new FinanceDAOImpl();
		}
		return instance;
	}
	
	@Override
	public void test() throws DBException {
//		try(Session session = Neo4jUtil.getDriver().session()){
//			Transaction trans = session.beginTransaction();
//			long countInsearted = 0;
//
//		}
	}
	
	
	
	
	
	
	
	
	
}
