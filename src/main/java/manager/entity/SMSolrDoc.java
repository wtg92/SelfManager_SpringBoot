package manager.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;

public abstract class SMSolrDoc implements Serializable {

    @Field
    @Id
    private String id;

    @Field
    private Long createUtc;

    @Field
    private Long updateUtc;

    /**
     * If the content in the _version_ field is greater than '1' (i.e., '12345'), then the _version_ in the document must match the _version_ in the index.
     * If the content in the _version_ field is equal to '1', then the document must simply exist. In this case, no version matching occurs, but if the document does not exist, the updates will be rejected.
     * If the content in the _version_ field is less than '0' (i.e., '-1'), then the document must not exist. In this case, no version matching occurs, but if the document exists, the updates will be rejected.
     * If the content in the _version_ field is equal to '0', then it doesn’t matter if the versions match or if the document exists or not. If it exists, it will be overwritten; if it does not exist, it will be added.
     */
    @Field
    private Long _version_;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getCreateUtc() {
        return createUtc;
    }

    public void setCreateUtc(Long createUtc) {
        this.createUtc = createUtc;
    }

    public Long getUpdateUtc() {
        return updateUtc;
    }

    public void setUpdateUtc(Long updateUtc) {
        this.updateUtc = updateUtc;
    }

    public Long get_version_() {
        return _version_;
    }

    public void set_version_(Long _version_) {
        this._version_ = _version_;
    }
}
