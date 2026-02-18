package manager.service;

import com.alibaba.fastjson2.JSON;
import manager.booster.SecurityBooster;
import manager.cache.CacheOperator;
import manager.dao.FilesDAO;
import manager.entity.general.FileRecord;
import manager.exception.LogicException;
import manager.system.SelfXDataSrcTypes;
import manager.system.SelfXErrors;
import manager.util.FileUtil;
import manager.util.locks.UserLockManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
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

    @Value("${s3.folder}")
    private String s3Folder;

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

    private String generateFullFileName(String suffix){
        return s3Folder+ "/" +UUID.randomUUID().toString()+"."+suffix;
    }

    public Map<String,Object> retrieveUploadURL(long loginId, Long sizeKB, String suffix,String srcParams) {
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
            final String fileName = generateFullFileName(suffix);
            record.setFileName(fileName);
            record.setOwnerId(loginId);
            record.setUploadStartUtc(System.currentTimeMillis());
            record.setPublic(true);
            record.setBucketName(S3Service.BUCKET_NAME);
            record.setSrcType(SelfXDataSrcTypes.BY_USERS);
            record.setSrcParams(srcParams);
            long id = dao.insertFileRecord(record);
            rlt.put("url",s3Service.generateUploadURL(fileName));
            rlt.put("id", securityBooster.encodeStableCommonId(id));
        });
        return rlt;
    }

    public String copyFileRecord(long loginId,String encodedID,Map<String,Object> srcParams){
        long id = securityBooster.getStableCommonId(encodedID);
        FileRecord fileRecord = cache.getFileRecord(id,()->dao.selectFileRecord(id));
        checkRecordPerms(fileRecord,loginId);
        //和create保持一致
        fileRecord.setOwnerId(loginId);
        fileRecord.setPublic(true);
        //改为复制
        fileRecord.setSrcType(SelfXDataSrcTypes.BY_COPYING_ACTION);
        fileRecord.setSrcParams(JSON.toJSONString(srcParams));
        //置空
        fileRecord.setId(null);

        long copyingId = dao.insertFileRecord(fileRecord);
        return securityBooster.encodeStableCommonId(copyingId);
    }

    public void uploadDoneNotify(long loginId, Long id) {
        locker.lockByUserAndClass(loginId,()->{
            FileRecord fileRecord = dao.selectFileRecord(id);
            fileRecord.setUploadDoneUtc(System.currentTimeMillis());
            fileRecord.setDone(true);
            cache.saveFileRecord(fileRecord,(one)->dao.updateExistedFileRecord(one));
        });
    }
    private static boolean isNotOwner(@Nullable  Long loginId, FileRecord fileRecord){
        if(loginId == null){
            return true;
        }
        return !loginId.equals(fileRecord.getOwnerId());
    }

    private static void checkRecordPerms(FileRecord fileRecord,@Nullable  Long loginId){
        if((!fileRecord.getPublic()) && isNotOwner(loginId, fileRecord)){
            throw new LogicException(SelfXErrors.SEE_PRIVATE_IMG);
        }
    }

    public Map<String,Object> retrieveGetURL(@Nullable  Long loginId, Long id) {
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
            cache.deleteEntity(id,()->dao.deleteFileRecord(id),cache::removeFileRecord);
            long num = dao.countFileRecordsByBucketNameAndFileName(fileRecord.getBucketName(),fileRecord.getFileName());
            if(num == 0){
                s3Service.deleteObject(fileRecord);
            }
        });
    }

    public FileRecord getRecord(@Nullable Long loginId, Long id) {
        FileRecord fileRecord = cache.getFileRecord(id,()->dao.selectFileRecord(id));
        checkRecordPerms(fileRecord,loginId);
        // 不暴露实际ID
        fileRecord.setId(null);
        fileRecord.setOwnerId(null);
        fileRecord.setBucketName(null);
        return fileRecord;
    }
}
