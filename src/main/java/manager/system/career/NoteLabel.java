package manager.system.career;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import manager.exception.NoSuchElement;
import manager.util.SystemUtil;

/**
 * 对于该枚举，Color 应该放到枚举里，因为这是它独有的，不存在复用的问题。
 * 该枚举 无需要DBCode 是靠name来唯一标志的
 * */
public enum NoteLabel {
	UNDECIDED("",""),
	EM("EM","强调，点击复制"),
	IDEA("IDEA","想法，灵感"),
	TODO("TODO","待办，点击变成DONE"),
	DONE("DONE","待办的完成态，点击退回为TODO"),
	/*代表着memo里选择无的Item*/
	SPEC_NULL("无","")
	;
	
	private String name;
	private String desc;
	
	public static List<NoteLabel> loadCommonLabels(){
		return Arrays.stream(NoteLabel.values()).filter(label->label != UNDECIDED && label != SPEC_NULL).collect(Collectors.toList());
	}
	
	private NoteLabel(String name,String desc) {
		this.name = name;
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public static NoteLabel valueOfName(String name) {
		try {
			return SystemUtil.valueOfField(name,e->e.getName(), NoteLabel.class);
		} catch (NoSuchElement e) {
			assert false : name;
			return NoteLabel.UNDECIDED;
		}
	}
	
}
