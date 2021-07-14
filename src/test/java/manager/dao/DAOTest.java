package manager.dao;

import java.util.Calendar;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;

import manager.entity.general.User;
import manager.util.DBUtil;

public class DAOTest {
	
	
	@Before
	public void setUp() {
		DAOFactory.deleteAllTables();
	}
	
	
}
