package manager.dao.impl;

import static manager.util.DBUtil.getHibernateSessionFactory;

import java.util.List;

import org.hibernate.SessionFactory;

import manager.dao.EntityTagDAO;
import manager.entity.general.TagSet;
import manager.exception.DBException;
import manager.system.TagType;

public class EntityTagDaoImpl implements EntityTagDAO{
	
	private final SessionFactory hbFactory = getHibernateSessionFactory();

	@Override
	public int insertTagSet(TagSet tagSet) throws DBException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateExistedTagSet(TagSet tagSet) throws DBException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<TagSet> selectTagSetByUserAndTypes(int userId, List<TagType> types) throws DBException {
		// TODO Auto-generated method stub
		return null;
	}

}
