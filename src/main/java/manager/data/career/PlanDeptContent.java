package manager.data.career;

import java.util.ArrayList;
import java.util.List;

import manager.data.proxy.career.CareerLogProxy;
import manager.entity.virtual.career.PlanDeptItem;

public class PlanDeptContent {
	public List<PlanDeptItem> items = new ArrayList<>();
	public List<CareerLogProxy> logs = new ArrayList<>();
}
