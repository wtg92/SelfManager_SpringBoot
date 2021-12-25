package manager.dao;

import java.util.List;

import manager.entity.general.TagSet;
import manager.exception.DBException;
import manager.system.TagType;

public interface EntityTagDAO {
	
	int insertTagSet(TagSet tagSet) throws DBException;
	void updateExistedTagSet(TagSet tagSet) throws DBException;
	List<TagSet> selectTagSetByUserAndTypes(int userId,List<TagType> types) throws DBException;
}
