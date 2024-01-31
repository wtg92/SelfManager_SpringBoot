package manager.dao;

import manager.util.DBUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.schema.Action;
import org.junit.Test;

import manager.TestUtil;
import manager.exception.DBException;
import manager.exception.LogicException;

import java.util.Map;

import static org.hibernate.cfg.JdbcSettings.*;
import static org.hibernate.cfg.JdbcSettings.HIGHLIGHT_SQL;


public class DEBUG_DBCOMMON {



	@Test
	public void initConnection()  {

		Configuration configuration = new Configuration();

//				.setProperty(JAKARTA_JDBC_URL, "jdbc:mysql://localhost:3306/scientific_manager")
//				.setProperty(JAKARTA_JDBC_USER, "root")
//				.setProperty(JAKARTA_JDBC_PASSWORD, "Expensivewin110")
//				.setProperty(JAKARTA_JDBC_DRIVER, "com.mysql.cj.jdbc.Driver")
//				.setProperty(DIALECT, "org.hibernate.dialect.MySQLDialect")
//				.buildSessionFactory();
	}


	
}
