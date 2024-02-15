package manager.servlet;
import static manager.system.SMParm.OP;
import static manager.util.UIUtil.getNonNullParam;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.exception.LogicException;
import manager.exception.SMException;
import manager.system.SMOP;

public abstract class SMServlet extends HttpServlet {
	final private static Logger logger = Logger.getLogger(SMServlet.class.getName());

	private static final long serialVersionUID = 2442482414960821919L;
	
	public abstract String process(HttpServletRequest request) throws SMException, ServletException, IOException;
	    
	    @Override
	    protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    	logger.log(Level.WARNING,"Shouldn't use GET!");
	        doPost(request, response);
	    }
	    
	    @Override
	    protected final void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    	
	        response.setCharacterEncoding("UTF-8");
	        response.setContentType("application/json");
	        long t1 = System.currentTimeMillis();
	        try {
	            String jsonResult = process(request);
	            /*如果json字符串里有换行符，将导致JSON解析失败，这里用br标签等效替换*/
	            /*TODO 当使用了fastJSON 这个处理好像没用了*/
	            jsonResult =jsonResult.replaceAll("(\\r\\n|\\n|\\n\\r)","<br/>");
	            
	            try(Writer writer = new BufferedWriter(response.getWriter(),1000)) {
	            	writer.write(jsonResult);
	            } catch (IOException e) {
	            	logger.log(Level.SEVERE,"Shouldn't happen.  IOException during doPost:" + e.getMessage());
	    		}
	        } catch (SMException e) {
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
	        } catch (ServletException e) {
	            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
	        } finally {
	            long t2 = System.currentTimeMillis();
	            if(t2 - t1 >= 3000) {
					try {
						SMOP op = SMOP.valueOfName(getNonNullParam(request,OP));
						logger.log(Level.WARNING, "Found SLOW op: " + op.name() + "\t" + (t2-t1)+"ms.");
					} catch (LogicException e) {
						assert false;
						logger.log(Level.SEVERE,"超时+没配置OP "+(t2-t1)+"ms.");
					}
	              
	            }
	        }
	    }
}
