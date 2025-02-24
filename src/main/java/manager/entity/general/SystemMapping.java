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
public class SystemMapping extends SMGeneralEntity {

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
