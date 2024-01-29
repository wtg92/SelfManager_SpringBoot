package manager.entity;

import java.io.Serializable;


import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.GenericGenerator;

/**
  *    由于使用redis等Nosql数据库而把关系表也当做一种实体，因此区分了general包和relation包。
 *  general:通常意义的实体
 *  relation:对于十分特殊的情况（关系表除了关联id还有其它信息时），才将其视作实体，用来缓存。
 *    出于性能及扩展性考虑，关系表也用id为单主键
 *   关系的唯一性用逻辑来保证
 *   TODO 对于枚举需要做特殊处理
 * @author 王天戈
 */
@MappedSuperclass
public abstract class SMEntity implements Serializable{

	private static final long serialVersionUID = -5640352156603901321L;
	@Id
	@GeneratedValue(generator = "idGenerator", strategy = GenerationType.IDENTITY)
	@GenericGenerator(name = "idGenerator", strategy = "identity")
	private Long id;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
