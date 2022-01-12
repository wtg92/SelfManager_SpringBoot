package manager.util;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;

import manager.entity.general.SMGeneralEntity;

public abstract class Neo4jUtil {
	
	private static final Driver Driver = init();
	
	private static Driver init() {
		String url = CommonUtil.getValFromPropertiesFileInResource("neo4j_url");
		String pwd = CommonUtil.getValFromPropertiesFileInResource("neo4j_pwd");
		return GraphDatabase.driver("bolt://"+url,AuthTokens.basic("neo4j",pwd));
	}
	
	public static Driver getDriver() {
		return Driver;
	}
	
	
	public static long createOne(SMGeneralEntity one) {
		one.getClass();
		return 1;
	}
	
	
}
