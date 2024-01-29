package manager.servlet;

import static manager.system.SMParm.OP;
import static manager.util.UIUtil.getNonNullParam;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import manager.data.SingleFileUnit;
import manager.exception.LogicException;
import manager.exception.SMException;
import manager.logic.tool.ToolLogic;
import manager.system.SMError;
import manager.system.SMOP;
import manager.system.tool.Tool;
import manager.util.FileUtil;
import manager.util.UIUtil;

@WebServlet(name="DownloadServlet",urlPatterns = "/DownloadServlet")
public class DownloadServlet extends HttpServlet{
	final private static Logger logger = Logger.getLogger(DownloadServlet.class.getName());

	private static final long serialVersionUID = 5397538742188394627L;
	
	private ToolLogic tL = ToolLogic.getInstance();
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
        doPost(request, response); 
    }

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setCharacterEncoding("utf8");
		response.setContentType("application/force-download;charset=UTF-8");
		
		Tool target  = null;
		long t1 = System.currentTimeMillis();
		try {
			SMOP op = SMOP.valueOfName(getNonNullParam(request, OP));
			byte[] rlt = null;
			switch(op) {
			case T_EXTRACT_PPT_IMGAS:
				target  = Tool.IMGS_EXTRACTOR_OF_PPT;
				rlt = extractPPTImgs(request,response);
				break;
			case T_MODIFY_IMGS_DPI:
				target = Tool.IMGS_DPI_MODIFER;
				rlt = modifyImgsDPI(request,response);
				break;
			default:
				assert false : op.getName();
				throw new LogicException(SMError.UNKOWN_OP,getNonNullParam(request,OP));
			}
			
			try(OutputStream out = new BufferedOutputStream(response.getOutputStream(),10000);
					InputStream in = new BufferedInputStream(new ByteArrayInputStream(rlt), 10000)){
				FileUtil.copyWithoutBuffer(in, out);
			}
			
			if(target!=null) {
				tL.addToolRecordSucOnce(target);
			}
			
		} catch (SMException e) {
			/*假如是文件上传的问题，就不记录错误了*/
			if(target!=null && e.type != SMError.FIEL_UPLOADING_ERROR) {
				try {
					tL.addToolRecordFailOnce(target);
				} catch (SMException e1) {
					e1.printStackTrace();
					logger.log(Level.SEVERE,"add tool fail error");
				}
			}
			e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            
        } finally {
        	 long t2 = System.currentTimeMillis();
	            if(t2 - t1 >= 5000) {
					try {
						SMOP op = SMOP.valueOfName(getNonNullParam(request,OP));
						logger.log(Level.WARNING,"Found SLOW op: " + op.name() + "\t" + (t2-t1)+"ms.");
					} catch (LogicException e) {
						assert false;
						logger.log(Level.SEVERE,"超时+没配置OP "+(t2-t1)+"ms.");
					}
	              
	            }
        }
	
	}
	private byte[] modifyImgsDPI(HttpServletRequest request, HttpServletResponse response) throws LogicException {
//		SingleFileUnit file = parseSingleFile(request);


		return null;
	}

	private static void setFileName(HttpServletRequest request, HttpServletResponse response, String fileName) {
		response.setHeader("Content-Disposition", "attachment;filename="+ UIUtil.encodeFileName(fileName, request));
	}
	
	
	
	private byte[] extractPPTImgs(HttpServletRequest request, HttpServletResponse response) throws LogicException {
//		SingleFileUnit file = parseSingleFile(request);
		return tL.extractPPTImgs(null);
	}
	
	
}
