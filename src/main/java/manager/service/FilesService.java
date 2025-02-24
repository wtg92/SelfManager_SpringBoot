package manager.service;

import manager.cache.CacheOperator;
import manager.dao.FilesDAO;
import manager.entity.general.FileRecord;
import manager.exception.LogicException;
import manager.system.SMError;
import manager.util.FileUtil;
import manager.util.SecurityUtil;
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

    public Map<String,Object> retrieveUploadURL(long loginId, Long sizeKB, String suffix) {
        Map<String,Object> rlt = new HashMap<>();
        locker.lockByUserAndClass(loginId,()->{
            double sumGB = FileUtil.kbToGb(dao.selectSumKBSizeByOwner(loginId));
            if(sumGB > UPLOAD_MAX_GB_FOR_ONE_USER){
                throw new LogicException(SMError.MAX_UPLOAD_FILE_FOR_ONE_USER,sumGB,UPLOAD_MAX_GB_FOR_ONE_USER);
            }
            double mbForUpload = FileUtil.kbToMb(sizeKB);
            if(mbForUpload > UPLOAD_MAX_SIZE_OF_MB){
                throw new LogicException(SMError.UPLOAD_MAX_SIZE_OF_MB,mbForUpload,UPLOAD_MAX_SIZE_OF_MB);
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
            long id = dao.insertFileRecord(record);
            rlt.put("url",s3Service.generateUploadURL(fileName));
            rlt.put("id", SecurityUtil.encodeInfo(id));
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
    private static boolean isOwner (Long loginId,FileRecord fileRecord){
        return loginId.equals(fileRecord.getOwnerId());
    }

    public Map<String,Object> retrieveGetURL(long loginId, Long id) {
        FileRecord fileRecord = dao.selectFileRecord(id);
        if((!fileRecord.getPublic()) && !isOwner(loginId,fileRecord)){
            throw new LogicException(SMError.SEE_PRIVATE_IMG);
        }
        Map<String,Object> rlt = new HashMap<>();
        rlt.put("url",s3Service.generateGetURL(fileRecord));
        rlt.put("suffix",fileRecord.getSuffix());
        rlt.put("fileName",fileRecord.getFileName());
        return rlt;
    }


    public void deleteFileRecord(long loginId, Long id) {
        FileRecord fileRecord = dao.selectFileRecord(id);
        if(!isOwner(loginId,fileRecord)){
            /**
             * 东西只能本人删
             */
            throw new LogicException(SMError.UNEXPECTED_ERROR);
        }
        // 这个删除S3上的 和单个用户无关 和所有用户有关
        locker.lockByClass(()->{
            dao.deleteFileRecord(id);
            long num = dao.countFileRecordsByBucketNameAndFileName(fileRecord.getBucketName(),fileRecord.getFileName());
            if(num == 0){
                s3Service.deleteObject(fileRecord);
            }
        });
    }
}
