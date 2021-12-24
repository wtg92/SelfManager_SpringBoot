package manager.system.tool.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import manager.system.tool.Tool;


@Converter
public class ToolConverter implements AttributeConverter<Tool, Integer> {

	@Override
	public Tool convertToEntityAttribute(Integer attribute) {
		return Tool.valueOfDBCode(attribute);
	}

	@Override
	public Integer convertToDatabaseColumn(Tool dbData) {
		return dbData.getDbCode();
	}
}
