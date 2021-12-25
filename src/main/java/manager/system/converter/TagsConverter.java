package manager.system.converter;

import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import manager.data.EntityTag;
import manager.logic.sub.TagCalculator;


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
