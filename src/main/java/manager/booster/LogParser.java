package manager.booster;

import java.util.List;

import manager.entity.virtual.career.BalanceItem;
import manager.system.career.CareerLogAction;
import manager.system.career.PlanItemType;
import manager.system.career.PlanState;
import manager.system.career.WorkSheetState;
import manager.util.CommonUtil;
import manager.util.TimeUtil;

public abstract class LogParser {
	
	/*还是静态化  id的转化 交给逻辑吧*/
	public static String parse(CareerLogAction action,List<String> params) {
		switch(action) {
		case ADD_ROOT_ITEM_TO_PLAN:
			return String.format("添加计划项%s",
					fillEmLabel(calculateRootPlanItemMes(params.get(0), params.get(1),params.get(2))));

		case ADD_SON_ITEM_TO_PLAN:{
			String fatherCatName = params.get(3);
			return String.format("在 %s下添加从属计划项 \n%s",fillEmLabel(fatherCatName),fillEmLabel(calculateSonPlanItemMes(params.get(0),params.get(1)
					,params.get(2),fatherCatName,params.get(4))));
		}
		case REMOVE_ITEM_FROM_PLAN_AS_FATHER:
			return String.format("删除计划项%s", fillEmLabel(params.get(0)));
		case REMOVE_ITEM_FROM_PLAN_DUE_TO_FATHER_REMOVED:
			return String.format("由于所属项%s 被删除，删除%s", fillEmLabel(params.get(0)),fillEmLabel(params.get(1)));
		case UPDATE_ROOT_PLAN_ITEM:
			return String.format("将计划项%s修改为 %s", 
					fillEmLabel(calculateRootPlanItemMes(params.get(1),params.get(2), params.get(0))),
					fillEmLabel(calculateRootPlanItemMes(params.get(3),params.get(4), params.get(0))));
		case UPDATE_SON_PLAN_ITEM:{
			String fatherCatName = params.get(0);
			String fatherType = params.get(1);
			String sonType = params.get(2);
			
			return String.format("将计划项 %s修改为%s", 
					fillEmLabel(calculateSonPlanItemMes(params.get(3), params.get(4), sonType, fatherCatName, fatherType)),
					fillEmLabel(calculateSonPlanItemMes(params.get(5), params.get(6), sonType, fatherCatName, fatherType)));
		}
		case PLAN_STATE_CHANGED_BY_DATE:
			return String.format("根据开始日期 %s和结束日期 %s，由于时间到了，将状态由 %s修改为%s",
					fillEmLabel(params.get(0)),
					fillEmLabel(calculatePlanEndDate(params.get(1))),
					fillEmLabel(PlanState.valueOfDBCode(params.get(2)).getName()),
					fillEmLabel(PlanState.valueOfDBCode(params.get(3)).getName()));
			
		case CREATE_PLAN:
			return String.format("创建计划%s，根据开始日期和结束日期，创建时计划的状态为%s",
					fillEmLabel(calcaulatePlanMes(params.get(0),params.get(1),params.get(2))),
					fillEmLabel(PlanState.valueOfDBCode(params.get(3)).getName()));
		case SAVE_PLAN:
			return String.format("将计划%s修改为%s",
					fillEmLabel(calcaulatePlanMes(params.get(0), params.get(1), params.get(2))),
					fillEmLabel(calcaulatePlanMes(params.get(3), params.get(4), params.get(5))));
		case STATE_CHENGED_DUE_TO_SAVING_PLAN:
			return String.format("由于在保存时选择了计算计划状态，经重新计算，将状态由%s修改为%s",
					fillEmLabel(PlanState.valueOfDBCode(params.get(0)).getName()),
					fillEmLabel(PlanState.valueOfDBCode(params.get(1)).getName()));
		case ABANDON_PLAN:
			return String.format("由于手动废弃了计划，将状态由%s修改为%s ，结束日期由%s修改为%s",
					fillEmLabel(PlanState.valueOfDBCode(params.get(0)).getName()),
					fillEmLabel(PlanState.valueOfDBCode(params.get(1)).getName()),
					fillEmLabel(calculatePlanEndDate(params.get(2))),
					fillEmLabel(calculatePlanEndDate(params.get(3))));
		case FINISH_PLAN:
			return String.format("由于手动完成了计划，将状态由%s修改为%s ，结束日期由%s修改为%s",
					fillEmLabel(PlanState.valueOfDBCode(params.get(0)).getName()),
					fillEmLabel(PlanState.valueOfDBCode(params.get(1)).getName()),
					fillEmLabel(calculatePlanEndDate(params.get(2))),
					fillEmLabel(calculatePlanEndDate(params.get(3))));
		
		case WS_STATE_CHANGED_BY_DATE:
			return String.format("由于时间到了且存在未完成的计划项，将状态由%s 修改为%s",
					fillEmLabel(WorkSheetState.valueOfDBCode(params.get(0)).getName()),
					fillEmLabel(WorkSheetState.valueOfDBCode(params.get(1)).getName()));
		
		case OPEN_WS_TODAY:
			return String.format("基于%s开启工作表，开启时工作表的状态是%s",
					fillEmLabel(params.get(0)),
					fillEmLabel(WorkSheetState.valueOfDBCode(params.get(1)).getName()));
		
		case WS_STATE_CHENGED_DUE_TO_ITEM_MODIFIED:
			return String.format("由于计划项或工作项发生变化，重新计算工作表状态，状态由%s修改为%s",
					fillEmLabel(WorkSheetState.valueOfDBCode(params.get(0)).getName()),
					fillEmLabel(WorkSheetState.valueOfDBCode(params.get(1)).getName()));
		
		case CREATE_PLAN_DEPT:
			return String.format("查看或同步了历史账单，账单被创建");
		
		case SYNC_ITEM_FOR_DEPT:{
			String type = PlanItemType.valueOfDBCode(params.get(4)).getName();
			return String.format("与%s工作表的%s进行同步，使得该项的欠账由 %s 变为 %s",
					fillEmLabel(params.get(0)),
					fillEmLabel(params.get(1)),
					fillEmLabel(params.get(2)+type),
					fillEmLabel(params.get(3))+type);
		}
		
		case REMOVE_DEPT_ITEM_DUE_TO_ZERO_VAL:
			return String.format("由于欠账项%s被同步为0，该项抵消",
					fillEmLabel(params.get(0)));
			
		case ADD_DEPT_ITEM:{
			String type = PlanItemType.valueOfDBCode(params.get(3)).getName();
			return String.format("与%s工作表的%s进行同步，生成对应欠账项 %s",
					fillEmLabel(params.get(0)),
					fillEmLabel(params.get(1)),
					fillEmLabel(params.get(2)+type));
		}
		
		case MODIFY_DEPT_ITEM_VAL:{
			String type = PlanItemType.valueOfDBCode(params.get(0)).getName();
			
			return String.format("手动将欠账项%s由%s修改为%s", 
					fillEmLabel(params.get(1)),
					fillEmLabel(params.get(2)+type),
					fillEmLabel(params.get(3)+type));
		}
		
		case MODIFY_DEPT_ITEM_VAL_AND_NAME : {
			String type = params.get(0);
			
			return String.format("手动将欠账项%s修改为%s", 
					fillEmLabel(calcaulatePlanDeptItemMes(params.get(1), params.get(2), type)),
					fillEmLabel(calcaulatePlanDeptItemMes(params.get(3), params.get(4), type)));
		}
			
		case MODIFY_DEPT_ITEM_NAME_CAUSE_MERGE : {
			String type = params.get(0);
			
			return String.format("手动将欠账项%s修改为%s,发现存在同名项%s,合并后为%s", 
					fillEmLabel(calcaulatePlanDeptItemMes(params.get(1), params.get(2), type)),
					fillEmLabel(calcaulatePlanDeptItemMes(params.get(3), params.get(4), type)),
					fillEmLabel(calcaulatePlanDeptItemMes(params.get(5), params.get(6), type)),
					fillEmLabel(calcaulatePlanDeptItemMes(params.get(5), params.get(7), type)));
		}
		
		case CLEAR_DEPT_LOGS_WHEN_TOO_MUCH : {
			return String.format("由于历史欠账Log达到了限量%s,清理较早的%s条，执行快照：%s", 
					fillEmLabel(params.get(0)),
					fillEmLabel(params.get(1)),
					fillEmLabel(params.get(2)));
		}
		case COPY_PLAN_ITEMS:
			return String.format("复制了计划%s的所有计划项 ", 
					fillEmLabel(params.get(0)));
		
		
		default:
			assert false : "未配置的action" + action;
			return "";
		
		}
	}
	
	
	private static String fillEmLabel(String text) {
		return String.format("<em>%s</em>", text);
	}
	
	public static String getSnapshot(BalanceItem item) {
		return calcaulatePlanDeptItemMes(item.getName(), String.valueOf(CommonUtil.fixDouble(item.getValue())), String.valueOf(item.getType().getDbCode()));
	}
	
	private static String calculatePlanEndDate(String endDate) {
		if(TimeUtil.isBlank(TimeUtil.parseDate(endDate))) {
			return "至今";
		};
		return endDate;
	}
	private static String calcaulatePlanDeptItemMes(String name,String val,String typeCode) {
		return String.format("%s %s%s",name,val, PlanItemType.valueOfDBCode(typeCode).getName());
	}
	
	private static String calcaulatePlanMes(String name,String startDate,String endDate) {
		return String.format("%s 开始日期 %s  结束日期 %s",name,startDate,calculatePlanEndDate(endDate));
	}
	
	private static String calculateRootPlanItemMes(String catName, int value, PlanItemType type) {
		return String.format("%s 投入  %s %s",catName,value,type.getName());
	}
	
	private static String calculateRootPlanItemMes(String catName, String value, String type) {
		return calculateRootPlanItemMes(catName,Integer.parseInt(value),PlanItemType.valueOfDBCode(type));
	}
	
	private static String calculateSonPlanItemMes(String sonCatName, double mappingVal, PlanItemType sonType, String fatherCatName, PlanItemType fatherType) {
		if(sonType == fatherType) {
			return String.format("%s 换抵比率 %s", sonCatName, mappingVal*100 +"%");
		}
		if(sonType == PlanItemType.MINUTES) {
			return String.format("%s 1次 %s 相当于  %s 分钟",sonCatName,fatherCatName,(int)mappingVal);
		}
		assert fatherType == PlanItemType.MINUTES;
		return String.format("%s (1次) 抵 %s 的 %s 分钟",sonCatName,fatherCatName,(int)mappingVal);
	}
	
	private static String calculateSonPlanItemMes(String sonCatName, String mappingVal, String sonType, String fatherCatName, String fatherType) {
		return calculateSonPlanItemMes(sonCatName, Double.parseDouble(mappingVal), PlanItemType.valueOfDBCode(sonType), fatherCatName, PlanItemType.valueOfDBCode(fatherType));
	}
}
