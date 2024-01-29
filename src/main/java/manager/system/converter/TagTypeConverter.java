package manager.system.converter;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import manager.system.TagType;

@Converter
public class TagTypeConverter implements AttributeConverter<TagType, Integer> {

	@Override
	public TagType convertToEntityAttribute(Integer attribute) {
		return TagType.valueOfDBCode(attribute);
	}

	@Override
	public Integer convertToDatabaseColumn(TagType dbData) {
		return dbData.getDbCode();
	}
}
