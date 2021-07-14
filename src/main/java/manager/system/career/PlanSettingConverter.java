package manager.system.career;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

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
