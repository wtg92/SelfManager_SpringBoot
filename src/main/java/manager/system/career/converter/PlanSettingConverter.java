package manager.system.career.converter;

import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import manager.system.career.PlanSetting;
import manager.util.SystemUtil;


@Converter
public class PlanSettingConverter implements AttributeConverter<List<PlanSetting>, Integer> {

	@Override
	public List<PlanSetting> convertToEntityAttribute(Integer attribute) {
		if(attribute == null)
			return new ArrayList<>();
		
		return SystemUtil.parseToList(attribute, PlanSetting::valueOfDBCode);
	}

	@Override
	public Integer convertToDatabaseColumn(List<PlanSetting> dbData) {
		return SystemUtil.parseToEnumsCode(dbData, PlanSetting::getDbCode);
	}
}
