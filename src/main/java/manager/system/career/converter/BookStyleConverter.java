package manager.system.career.converter;



import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import manager.system.books.BookStyle;



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
