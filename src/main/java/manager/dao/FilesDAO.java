package manager.dao;

import manager.entity.general.FileRecord;
import manager.entity.general.User;
import manager.entity.general.career.Plan;
import manager.entity.general.career.WorkSheet;
import manager.exception.DBException;
import manager.exception.NoSuchElement;
import manager.system.DBConstants;
import manager.system.career.PlanState;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

import java.util.List;

import static manager.util.DBUtil.*;

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
