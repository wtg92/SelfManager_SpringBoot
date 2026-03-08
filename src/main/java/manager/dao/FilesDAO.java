package manager.dao;

import manager.entity.general.FileRecord;
import manager.system.DBConstants;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

import static manager.util.DBUtil.countByBiFields;
import static manager.util.DBUtil.deleteEntity;
import static manager.util.DBUtil.executeSqlAndGetUniqueResult;
import static manager.util.DBUtil.insertEntity;
import static manager.util.DBUtil.selectEntitiesByField;
import static manager.util.DBUtil.selectExistedEntity;
import static manager.util.DBUtil.updateExistedEntity;

@Repository
public class FilesDAO {

    @Resource
    private SessionFactory sessionFactory;

    public FileRecord selectFileRecord(long id) {
        return selectExistedEntity(id, FileRecord.class, sessionFactory);
    }

    public long insertFileRecord(FileRecord fileRecord){
        return insertEntity(fileRecord, sessionFactory);
    }

    public void deleteFileRecord(long id){
        deleteEntity(FileRecord.class, id, sessionFactory);
    }

    public List<FileRecord> selectFileRecordsByOwner(long ownerId){
        return selectEntitiesByField(FileRecord.class, DBConstants.F_OWNER_ID,ownerId, sessionFactory);
    }

    public void updateExistedFileRecord(FileRecord fileRecord){
        updateExistedEntity(fileRecord, sessionFactory);
    }

    public long selectSumKBSizeByOwner(long loginId) {
        return executeSqlAndGetUniqueResult(sessionFactory,
                String.format("""
                        SELECT IFNULL(SUM(size_kb), 0) AS total_size_kb
                        FROM %s
                        WHERE %s = %d;
                        """,DBConstants.T_FILE_RECORD,DBConstants.F_OWNER_ID,loginId)
                ,Long.class);
    }

    public long countFileRecordsByBucketNameAndFileName(String bucketName, String fileName) {
        return countByBiFields(DBConstants.T_FILE_RECORD,DBConstants.F_BUCKET_NAME,bucketName,DBConstants.F_FILE_NAME,fileName,sessionFactory);
    }
}
