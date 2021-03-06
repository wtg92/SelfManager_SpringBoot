package manager.servlet;

import static manager.system.SMParm.OP;
import static manager.system.SMParm.PDF;
import static manager.util.UIUtil.getNonNullParam;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import manager.exception.LogicException;
import manager.system.SM;
import manager.system.SMError;
import manager.system.SMOP;

@WebServlet(name="PDFServlet",urlPatterns = "/PDFServlet")
public class PDFServlet extends HttpServlet{
	final private static Logger logger = Logger.getLogger(PDFServlet.class.getName());

	private static final long serialVersionUID = 3781555755626946527L;

	  
    @Override
    protected final void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		long t1 = System.currentTimeMillis();
		response.setCharacterEncoding("utf8");
		response.setContentType("application/pdf");
		int buffer =  10000;
		try {
			SMOP op = SMOP.valueOfName(getNonNullParam(request, OP));
			String pdf = getNonNullParam(request, PDF);
			assert !pdf.endsWith(".pdf"):pdf;
			File folder = null;
			switch(op) {
			case PDF_GENERAL:
				folder = SM.PDF_FILES_FOLDER;
				break;
			default:
				assert false : op.getName();
				throw new LogicException(SMError.UNKOWN_OP,getNonNullParam(request,OP));
			}
			
	        ByteBuffer byteBuffer = ByteBuffer.allocate(buffer);
	        try(RandomAccessFile target = new RandomAccessFile(new File(folder, pdf+".pdf"),"r");
	        		FileChannel inChannel = target.getChannel();
	        		 OutputStream outputStream = new BufferedOutputStream(response.getOutputStream(), buffer);
	        		){
	        	while((inChannel.read(byteBuffer)) != -1 && byteBuffer.hasArray()){
	 	            outputStream.write(byteBuffer.array());
	 	            byteBuffer.clear();
	 	        }
	        };
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "??????PDF??????????????????????????????????????????PDF???????????????????????????");
		} finally {
			long t2 = System.currentTimeMillis();
			if (t2 - t1 >= 5000) {
				try {
					SMOP op = SMOP.valueOfName(getNonNullParam(request, OP));
					logger.log(Level.WARNING,"Found SLOW op: " + op.name() + "\t" + (t2 - t1) + "ms.");
				} catch (LogicException e) {
					assert false;
					logger.log(Level.SEVERE,"??????+?????????OP " + (t2 - t1) + "ms.");
				}

			}
		}

	}
}
