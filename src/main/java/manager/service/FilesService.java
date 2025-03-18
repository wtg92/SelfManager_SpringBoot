package manager.service;

import manager.cache.CacheOperator;
import manager.dao.FilesDAO;
import manager.entity.general.FileRecord;
import manager.exception.LogicException;
import manager.booster.SecurityBooster;
import manager.system.SelfXDataSrcTypes;
import manager.system.SelfXErrors;
import manager.util.FileUtil;
import manager.util.locks.UserLockManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FilesService {

    @Value("${s3.upload.maxMB}")
    private Integer UPLOAD_MAX_SIZE_OF_MB;

    @Value("${s3.upload.maxGBSumForOneUser}")
    private Integer UPLOAD_MAX_GB_FOR_ONE_USER;
    @Resource
    private UserLockManager locker;
    @Resource
    private S3Service s3Service;
    @Resource
    CacheOperator cache;
    @Resource
    private FilesDAO dao;

    @Resource
    private SecurityBooster securityBooster;


    public Map<String,Object> retrieveUploadURL(long loginId, Long sizeKB, String suffix) {
        Map<String,Object> rlt = new HashMap<>();
        locker.lockByUserAndClass(loginId,()->{
            double sumGB = FileUtil.kbToGb(dao.selectSumKBSizeByOwner(loginId));
            if(sumGB > UPLOAD_MAX_GB_FOR_ONE_USER){
                throw new LogicException(SelfXErrors.MAX_UPLOAD_FILE_FOR_ONE_USER,sumGB,UPLOAD_MAX_GB_FOR_ONE_USER);
            }
            double mbForUpload = FileUtil.kbToMb(sizeKB);
            if(mbForUpload > UPLOAD_MAX_SIZE_OF_MB){
                throw new LogicException(SelfXErrors.UPLOAD_MAX_SIZE_OF_MB,mbForUpload,UPLOAD_MAX_SIZE_OF_MB);
            }
            FileRecord record = new FileRecord();
            record.setSizeKb(sizeKB);
            record.setDone(false);
            record.setSuffix(suffix);
            final String fileName = UUID.randomUUID().toString()+"."+suffix;
            record.setFileName(fileName);
            record.setOwnerId(loginId);
            record.setUploadStartUtc(System.currentTimeMillis());
            record.setPublic(true);
            record.setBucketName(S3Service.BUCKET_NAME);
            record.setSrcType(SelfXDataSrcTypes.BY_USERS);
            long id = dao.insertFileRecord(record);
            rlt.put("url",s3Service.generateUploadURL(fileName));
            rlt.put("id", securityBooster.encodeStableCommonId(id));
        });
        return rlt;
    }

    public void uploadDoneNotify(long loginId, Long id) {
        locker.lockByUserAndClass(loginId,()->{
            FileRecord fileRecord = dao.selectFileRecord(id);
            fileRecord.setUploadDoneUtc(System.currentTimeMillis());
            fileRecord.setDone(true);
            cache.saveFileRecord(fileRecord,(one)->dao.updateExistedFileRecord(one));
        });
    }
    private static boolean isNotOwner(long loginId, FileRecord fileRecord){
        if(loginId == 0){
            return true;
        }
        return loginId != fileRecord.getOwnerId();
    }

    private static void checkRecordPerms(FileRecord fileRecord,long loginId){
        if((!fileRecord.getPublic()) && isNotOwner(loginId, fileRecord)){
            throw new LogicException(SelfXErrors.SEE_PRIVATE_IMG);
        }
    }

    public Map<String,Object> retrieveGetURL(long loginId, Long id) {
        FileRecord fileRecord = cache.getFileRecord(id,()->dao.selectFileRecord(id));
        checkRecordPerms(fileRecord,loginId);
        Map<String,Object> rlt = new HashMap<>();
        rlt.put("url",s3Service.generateGetURL(fileRecord));
        rlt.put("suffix",fileRecord.getSuffix());
        rlt.put("fileName",fileRecord.getFileName());
        return rlt;
    }


    public void deleteFileRecord(long loginId, Long id) {
        FileRecord fileRecord = cache.getFileRecord(id,()->dao.selectFileRecord(id));
        if(isNotOwner(loginId, fileRecord)){
            /**
             * 东西只能本人删
             */
            throw new LogicException(SelfXErrors.UNEXPECTED_ERROR);
        }
        // 这个删除S3上的 和单个用户无关 和所有用户有关
        locker.lockByClass(()->{
            cache.deleteFileRecord(id,()->dao.deleteFileRecord(id));
            long num = dao.countFileRecordsByBucketNameAndFileName(fileRecord.getBucketName(),fileRecord.getFileName());
            if(num == 0){
                s3Service.deleteObject(fileRecord);
            }
        });
    }

    public FileRecord getRecord(long loginId, Long id) {
        FileRecord fileRecord = cache.getFileRecord(id,()->dao.selectFileRecord(id));
        checkRecordPerms(fileRecord,loginId);
        // 不暴露实际ID
        fileRecord.setId(null);
        fileRecord.setOwnerId(null);
        fileRecord.setBucketName(null);
        return fileRecord;
    }
}
