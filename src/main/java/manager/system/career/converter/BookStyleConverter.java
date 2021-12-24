package manager.system.career.converter;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import manager.system.career.BookStyle;



@Converter
public class BookStyleConverter implements AttributeConverter<BookStyle, Integer> {

	@Override
	public BookStyle convertToEntityAttribute(Integer attribute) {
		return BookStyle.valueOfDBCode(attribute);
	}

	@Override
	public Integer convertToDatabaseColumn(BookStyle dbData) {
		return dbData.getDbCode();
	}
}
