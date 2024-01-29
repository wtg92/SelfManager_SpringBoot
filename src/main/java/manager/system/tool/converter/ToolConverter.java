package manager.system.tool.converter;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
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
