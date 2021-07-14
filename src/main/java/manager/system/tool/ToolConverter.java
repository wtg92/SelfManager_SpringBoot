package manager.system.tool;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


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
