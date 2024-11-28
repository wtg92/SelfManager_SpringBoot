package manager.service.tool;

import jep.Interpreter;
import jep.JepConfig;
import jep.JepException;
import jep.SubInterpreter;
import manager.data.SingleFileUnit;
import manager.data.proxy.tool.ToolRecordProxy;
import manager.data.tool.ToolRecordSummary;
import manager.exception.LogicException;
import manager.exception.SMException;
import manager.service.UserLogic;
import manager.system.tool.Tool;

public abstract class ToolLogic {

	private static ToolLogic instance = null;

	UserLogic uL = UserLogic.getInstance();

	public abstract ToolRecordSummary loadToolRecordSummary(long loginerId) throws SMException;

	public abstract ToolRecordProxy loadToolRecord(Tool tool) throws SMException;

	public abstract byte[] extractPPTImgs(SingleFileUnit file) throws LogicException;
	public abstract byte[] modifyImgsDPI(SingleFileUnit file,int horizontalInch,int verticalInch) throws SMException;
	
	
	public abstract void addToolRecordSucOnce(Tool tool) throws SMException;

	public abstract void addToolRecordFailOnce(Tool tool) throws SMException;

	/*
	 * =================================================NOT ABSTRACT==============================================================
	 */

	public static synchronized ToolLogic getInstance() {
		if (instance == null) {
			instance = new ToolLogicImpl();
		}
		return instance;
	}
	
	
	
	
	
	/**
	 * 真诡异，Jep引入Python 假如Python名 没有下划线就没用？？？
	 * 
	 * @throws JepException
	 */
	public static void testPython_ONLYFORTEST() throws JepException {
		JepConfig jepConfig = new JepConfig();
		jepConfig.addIncludePaths("src/main/python/tool");

		try (Interpreter interp = new SubInterpreter(jepConfig)) {
			interp.eval("from imgs_dpi_modifier import *");
			Object object = interp.invoke("modifyImgDPI",100,100,100);
			System.out.println((String) object);
		}
		
		
//		try (Interpreter interp = new SharedInterpreter()) {
//		    interp.exec("from java.lang import System");
//		    interp.exec("s = 'Hello World'");
//		    interp.exec("System.out.println(s)");
//		    interp.exec("print(s)");
//		    interp.exec("print(s[1:-1])");
//		}
	};

}
