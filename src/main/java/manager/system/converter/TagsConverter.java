package manager.system.converter;

import java.util.List;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import manager.data.EntityTag;
import manager.booster.TagCalculator;


@Converter
public class TagsConverter implements AttributeConverter<List<EntityTag>, String> {

	@Override
	public List<EntityTag> convertToEntityAttribute(String attribute) {
		return TagCalculator.parseToTags(attribute);
	}

	@Override
	public String convertToDatabaseColumn(List<EntityTag> tags) {
		return TagCalculator.mergeTags(tags);
	}
}
