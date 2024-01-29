package manager.system.career.converter;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import manager.system.career.PlanState;


@Converter
public class PlanStateConverter implements AttributeConverter<PlanState, Integer> {

	@Override
	public PlanState convertToEntityAttribute(Integer attribute) {
		return PlanState.valueOfDBCode(attribute);
	}

	@Override
	public Integer convertToDatabaseColumn(PlanState dbData) {
		return dbData.getDbCode();
	}
}
