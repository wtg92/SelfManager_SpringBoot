package manager.data.worksheet;

import java.util.ArrayList;
import java.util.List;

import manager.data.proxy.career.CareerLogProxy;
import manager.entity.virtual.career.PlanItem;

public class PlanContent {
	public List<PlanItem> items = new ArrayList<>();
	public List<CareerLogProxy> logs = new ArrayList<>();
}
