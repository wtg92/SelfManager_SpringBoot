package manager.entity.general.note;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import manager.entity.general.SMGeneralEntity;
import manager.system.SMDB;

@Entity
@Table(name = SMDB.E_SHARING_BOOK)
public class SharingBook extends SMGeneralEntity {
}
