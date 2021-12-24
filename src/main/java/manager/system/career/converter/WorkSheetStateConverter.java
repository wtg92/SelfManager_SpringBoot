package manager.system.career.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import manager.system.career.WorkSheetState;



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
