package manager.util;

import java.util.HashMap;
import java.util.Map;

public abstract class ColorUtil {
	
	private static final Map<String,String> COLOR_MAPPER = init();

	private static Map<String, String> init() {
		Map<String,String> mapper = new HashMap<String, String>();
		/*蓝色*/
		mapper.put("进行中", "#007bff");
		/*灰色*/
		mapper.put("废弃", "#6c757d");
		/*绿色*/
		mapper.put("完成", "#28a745");
		/*青蓝色*/
		mapper.put("预备", "rgb(111, 116, 241)");
		/*红色*/
		mapper.put("超期", "#e48383");
		/*黄色*/
		mapper.put("不监控", "#ffc107");
		/*橙色*/
		mapper.put("超额完成", "rgb(245, 200, 0)");
		return mapper;
	}
	
	public static String getColor(String text) {
		return COLOR_MAPPER.getOrDefault(text, "#fff");
	}
}
