package manager.util;

import java.util.Calendar;

import org.junit.Test;

import manager.entity.general.finance.Asset;


public class DEBUG_Neo4jUtil {
	
	
	@Test
	public void testTransferToNeo4j() {
		Asset one = new Asset();
		one.setName("Test");
		String rlt = Neo4jUtil.transferToNeo4jObj(one,"a");
		System.out.println(rlt);
	}
	
}
