package manager.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.hibernate.Session;

import manager.dao.career.NoteDAO;
import manager.dao.career.WorkDAO;
import manager.dao.career.impl.NoteDAOImpl;
import manager.dao.career.impl.WorkDAOImpl;
import manager.dao.finance.FinanceDAO;
import manager.dao.finance.impl.FinanceDAOImpl;
import manager.dao.impl.UserDAOImpl;
import manager.dao.tool.ToolDAO;
import manager.dao.tool.impl.ToolDAOImpl;
import manager.system.SMDB;
import manager.util.DBUtil;

/**
 * 
 * update 不使用merge 而使用 update : merge会导致假如传入的对象id错误，则会给数据库添加错误数据 update则能把问题暴露出来
 *  	   会抛出OptimisticLockException 上层需要处理，真实环境中认为是发生了并发操作。 但实际还有一种可能,update的id不存在（hibernate机制缺陷导致的问题）
 *  	 TODO 但其实还是好难受，updateExistedXX 没有 updateXX 对应 和select 不一致了。
 *  
 * select 使用get     经了解后发现，hibernate的懒加载存在问题：
 * 		a.使用过程中，不能commit transcation，这让代码不得不跨层 
 * 		b.况且在了解过程中，这个懒加载意义不大，节省的只是非常短时间内的内存消耗，真正的内存节省应当在取值上面。
 * 		c.而且貌似会导致N多问题，各种异常。
 *    
 * 		当使用 Unique 相关函数时，默认最多查出一条数据
 *            关系表对于枚举的映射，（假如需要的话）交给逻辑层，dao层不处理
  *   关系表维护：一个是感觉Hibernate平白增加了复杂度，一个是使用上比较麻烦，决定采用手写jdbc了，这样效率也最高
  *            与此同时，尽管由于noSql,所以把关系表也当做一种实体，但关系表不存在update，因此也没有并发问题，不需要hibernate的乐观锁管理
  *   实体的createTime和updateTime在DAO层进行维护
 *  
 * @author 王天戈
 *
 */
public abstract class DAOFactory {
	
	public static UserDAO getUserDAO() {
		return new UserDAOImpl();
	}
	
	public static WorkDAO getWorkDAO() {
		return new WorkDAOImpl();
	}
	
	public static NoteDAO getNoteDAO() {
		return new NoteDAOImpl();
	}
	
	public static ToolDAO getToolDAO() {
		return new ToolDAOImpl();
	}
	
	public static FinanceDAO getFinanceDAO() {
		return FinanceDAOImpl.getInstance();
	}
	
	public static void deleteAllTables() {
		for(String table:SMDB.ALL_TABLES) {
			deleteTable(table);
		}
	}
	
	public static void deleteTable(String tableName) {
	    try (Session session = DBUtil.getHibernateSessionFactory().openSession()) {
	        session.doWork(connection -> {
	        	/**
	        	 * 需要关掉外键约束，貌似选错了引擎而不得不手动关掉约束
	        	 */
	            try (PreparedStatement p1 = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=0");
	            		PreparedStatement p2 = connection.prepareStatement("TRUNCATE TABLE " + tableName);
	            		PreparedStatement p3 = connection.prepareStatement("SET FOREIGN_KEY_CHECKS=1")) {
	            	p1.execute();
	                p2.executeUpdate();
	                p3.execute();
	            } catch (SQLException e) {
	            	e.printStackTrace();
	            	assert false;
	            }
	        });
	    }
		
	}
}
