package manager.system.career;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;



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
