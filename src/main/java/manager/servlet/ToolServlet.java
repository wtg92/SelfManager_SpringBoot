package manager.servlet;

import static manager.system.SMParm.OP;
import static manager.system.SMParm.TOOL;
import static manager.util.UIUtil.getLoginId;
import static manager.util.UIUtil.getNonNullParam;
import static manager.util.UIUtil.getNonNullParamInInt;

import java.io.IOException;

import com.alibaba.fastjson2.JSON;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
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
		getLoginId(request);
		Tool tool = Tool.valueOfDBCode(getNonNullParamInInt(request, TOOL));
		return JSON.toJSONString(tL.loadToolRecord(tool));
	}

	private String loadToolRecordSummary(HttpServletRequest request) throws SMException {
		long loginerId = getLoginId(request);
		return JSON.toJSONString(tL.loadToolRecordSummary(loginerId));
	}

}