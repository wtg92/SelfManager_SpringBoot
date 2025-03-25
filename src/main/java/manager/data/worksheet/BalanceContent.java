package manager.data.worksheet;

import java.util.ArrayList;
import java.util.List;

import manager.data.proxy.career.CareerLogProxy;
import manager.entity.virtual.career.BalanceItem;

public class BalanceContent {
	public List<BalanceItem> items = new ArrayList<>();
	public List<CareerLogProxy> logs = new ArrayList<>();
}
