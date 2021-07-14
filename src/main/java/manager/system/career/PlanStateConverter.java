package manager.system.career;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;


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
