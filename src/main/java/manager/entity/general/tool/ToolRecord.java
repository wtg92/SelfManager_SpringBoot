package manager.entity.general.tool;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import manager.entity.general.SMGeneralEntity;
import manager.system.DBConstants;
import manager.system.tool.Tool;
import manager.system.tool.converter.ToolConverter;

@Entity
@Table(name = DBConstants.T_TOOL_RECORD)
@DynamicInsert
@DynamicUpdate
public class ToolRecord extends SMGeneralEntity{

	private static final long serialVersionUID = -481931910477613102L;
	
	
	@Column
	private String content;

	@Column
	@Convert(converter = ToolConverter.class)
	private Tool tool;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Tool getTool() {
		return tool;
	}

	public void setTool(Tool tool) {
		this.tool = tool;
	}
	
}	
