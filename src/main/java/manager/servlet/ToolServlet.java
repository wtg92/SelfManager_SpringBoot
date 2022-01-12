package manager.servlet;

import static manager.system.SMParm.OP;
import static manager.system.SMParm.TOOL;
import static manager.util.UIUtil.getLoginerId;
import static manager.util.UIUtil.getNonNullParam;
import static manager.util.UIUtil.getNonNullParamInInt;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;

import manager.exception.LogicException;
import manager.exception.SMException;
import manager.logic.tool.ToolLogic;
import manager.system.SMError;
import manager.system.SMOP;
import manager.system.tool.Tool;

@WebServlet(name="ToolServlet",urlPatterns = "/ToolServlet")
public class ToolServlet extends SMServlet{
	private static final long serialVersionUID = -566721941701587967L;
	
	private ToolLogic tL = ToolLogic.getInstance();
	
	private SerializeConfig toolConfig = createToolEnumConfig();
	
	@SuppressWarnings("unchecked")
	private SerializeConfig createToolEnumConfig() {
		SerializeConfig conf = new SerializeConfig();
		conf.configEnumAsJavaBean(Tool.class);
		return conf;
	}
	
	@Override
	public String process(HttpServletRequest request) throws ServletException, IOException, SMException {
		SMOP op = SMOP.valueOfName(getNonNullParam(request,OP));
		switch (op) {
		case T_LOAD_TOOL_RECORD_SUMMARY:
			return loadToolRecordSummary(request);
		case T_LOAD_TOOL_RECORD:
			return loadToolRecord(request);
		default:
			assert false : op.getName();
			throw new LogicException(SMError.UNKOWN_OP,getNonNullParam(request,OP));
		}
	}
	
	private String loadToolRecord(HttpServletRequest request) throws SMException {
		getLoginerId(request);
		Tool tool = Tool.valueOfDBCode(getNonNullParamInInt(request, TOOL));
		return JSON.toJSONString(tL.loadToolRecord(tool),toolConfig); 
	}

	private String loadToolRecordSummary(HttpServletRequest request) throws SMException {
		long loginerId = getLoginerId(request);
		return JSON.toJSONString(tL.loadToolRecordSummary(loginerId),toolConfig); 
	}

}