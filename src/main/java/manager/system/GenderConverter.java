package manager.system;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class GenderConverter implements AttributeConverter<Gender, Integer> {

	@Override
	public Gender convertToEntityAttribute(Integer attribute) {
		return Gender.valueOfDBCode(attribute);
	}

	@Override
	public Integer convertToDatabaseColumn(Gender dbData) {
		return dbData.getDbCode();
	}
}
