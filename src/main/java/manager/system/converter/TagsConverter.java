package manager.system.converter;

import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import manager.logic.sub.TagCalculator;


@Converter
public class TagsConverter implements AttributeConverter<List<String>, String> {

	@Override
	public List<String> convertToEntityAttribute(String attribute) {
		return TagCalculator.parseToTags(attribute);
	}

	@Override
	public String convertToDatabaseColumn(List<String> tags) {
		return TagCalculator.mergeTags(tags);
	}
}
