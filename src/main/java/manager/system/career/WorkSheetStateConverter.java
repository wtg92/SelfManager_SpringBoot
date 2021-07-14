package manager.system.career;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;



@Converter
public class WorkSheetStateConverter implements AttributeConverter<WorkSheetState, Integer> {

	@Override
	public WorkSheetState convertToEntityAttribute(Integer attribute) {
		return WorkSheetState.valueOfDBCode(attribute);
	}

	@Override
	public Integer convertToDatabaseColumn(WorkSheetState dbData) {
		return dbData.getDbCode();
	}
}
