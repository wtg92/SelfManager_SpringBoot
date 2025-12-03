package manager.entity.general;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import manager.system.DBConstants;
import manager.util.CommonUtil;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table(name = DBConstants.T_FILE_RECORD)
@DynamicInsert
@DynamicUpdate
public class FileRecord extends SMGeneralEntity{

    @Column
    private Long sizeKb;

    @Column
    private Boolean done;

    @Column
    private String fileName;

    @Column
    private Long ownerId;

    /**
     * 这个视作 type
     * TODO 将来做个人文件管理时 以这个后缀来区分
     */
    @Column
    private String suffix;

    @Column
    private Long uploadStartUtc;

    @Column
    private Long uploadDoneUtc;

    @Column
    private Boolean isPublic;

    @Column
    private String bucketName;

    @Column
    private String srcType ;

    @Column
    private String srcParams;

    public String getSrcType() {
        return srcType;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public String getSrcParams() {
        return srcParams;
    }

    public void setSrcParams(String srcParams) {
        this.srcParams = srcParams;
    }

    public Long getSizeKb() {
        return sizeKb;
    }

    @Override
    public FileRecord clone(){
        return CommonUtil.deepClone(this);
    }

    public void setSizeKb(Long sizeKb) {
        this.sizeKb = sizeKb;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Long getUploadStartUtc() {
        return uploadStartUtc;
    }

    public void setUploadStartUtc(Long uploadStartUtc) {
        this.uploadStartUtc = uploadStartUtc;
    }

    public Long getUploadDoneUtc() {
        return uploadDoneUtc;
    }

    public void setUploadDoneUtc(Long uploadDoneUtc) {
        this.uploadDoneUtc = uploadDoneUtc;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }
}
