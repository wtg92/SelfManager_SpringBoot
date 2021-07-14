package manager.dao;

import org.junit.Test;

import manager.TestUtil;
import manager.entity.general.UserGroup;
import manager.exception.DBException;
import manager.exception.LogicException;
import manager.system.SM;
import manager.system.SMDB;

/**
  *  真实环境需要init跑的 
 * @author 王天戈
 *
 */
public class DEBUG_INIT {

	@Test
	public void init() throws DBException {
		TestUtil.initData();
	}
	
	
	@Test
	public void initForTest() throws DBException, LogicException{
		TestUtil.initEnvironment();
		TestUtil.initData();
		TestUtil.addAdmin();
	}
	
}
