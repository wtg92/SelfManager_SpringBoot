package manager.entity.general;

import jakarta.persistence.*;
import manager.system.DBConstants;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;

import java.io.Serializable;
@Entity
@Table(name = DBConstants.T_SYSTEM_MAPPING)
@DynamicInsert
@DynamicUpdate
public class SystemMapping implements Serializable,Cloneable {

    @Column
    private Long createUtc;
    @Column
    private Long creatorId;
    @Column
    private Long updateUtc;
    @Column
    private Long updaterId;

    @Id
    @GeneratedValue(generator = "idGenerator", strategy = GenerationType.IDENTITY)
    @GenericGenerator(name = "idGenerator", strategy = "identity")
    private Long id;

    @Column
    private String key;

    @Column
    private String value;

    @Column
    private String desc;

    @Override
    public SystemMapping clone() {
        try {
            return (SystemMapping) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public Long getCreateUtc() {
        return createUtc;
    }

    public void setCreateUtc(Long createUtc) {
        this.createUtc = createUtc;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getUpdateUtc() {
        return updateUtc;
    }

    public void setUpdateUtc(Long updateUtc) {
        this.updateUtc = updateUtc;
    }

    public Long getUpdaterId() {
        return updaterId;
    }

    public void setUpdaterId(Long updaterId) {
        this.updaterId = updaterId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
