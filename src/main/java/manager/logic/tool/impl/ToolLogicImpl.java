package manager.logic.tool.impl;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import jep.JepConfig;
import jep.JepException;
import jep.SubInterpreter;
import manager.dao.DAOFactory;
import manager.dao.tool.ToolDAO;
import manager.data.SingleFileUnit;
import manager.data.proxy.tool.ToolRecordProxy;
import manager.data.tool.ToolRecordSummary;
import manager.entity.general.tool.ToolRecord;
import manager.exception.LogicException;
import manager.exception.SMException;
import manager.logic.tool.ToolLogic;
import manager.logic.tool.sub.ToolRecordContentConverter;
import manager.system.SM;
import manager.system.SMError;
import manager.system.tool.Tool;
import manager.util.FileUtil;
import manager.util.POIUtil;
/**
 * 这个Tool 时间耗费在实际的工具运行（文件处理）中，
 * 因此不用缓存了 DAO的时间消耗相较之下  太少了
 * @author 王天戈
 */
public class ToolLogicImpl extends ToolLogic{
	
	final private static Logger logger = Logger.getLogger(ToolLogicImpl.class.getName());

	private ToolDAO tDAO = DAOFactory.getToolDAO();
	
	@Override
	public ToolRecordSummary loadToolRecordSummary(long loginerId) throws SMException {
		ToolRecordSummary summary = getOrInitToolRecordSummary();
		
		/*fastJson有BUG 嵌套的属性 无法映射全 诡异。。只能在调用的时候，重新fill一次了*/
		for(ToolRecordProxy proxy: summary.records) {
			fill(proxy);
		}
		
		return summary;
	}
	
	private void fill(ToolRecordProxy proxy) throws LogicException {
		proxy.content = ToolRecordContentConverter.convertToolRecord(proxy.record);
	}
	
	private static ToolRecordSummary calculateSummary(List<ToolRecord> records) throws LogicException {
		
		List<ToolRecordProxy> rlt = new ArrayList<ToolRecordProxy>();
		
		for(ToolRecord record:records) {
			ToolRecordProxy proxy = new ToolRecordProxy(record);
			rlt.add(proxy);
		}
		
		return new ToolRecordSummary(rlt);
	}
	
	
	private synchronized ToolRecordSummary getOrInitToolRecordSummary() throws SMException {
		Map<Tool,ToolRecord> records;
		try {
			records = tDAO.selectAllToolRecords().stream().collect(toMap(ToolRecord::getTool, Function.identity()));
		}catch(IllegalStateException e) {
			logger.log(Level.WARNING,"ToolRecords 出现重复项了？？？");
			return calculateSummary(tDAO.selectAllToolRecords());
		}
		 
		assert records.size() <= Tool.values().length-1;
		
		List<Tool> runningTools = Arrays.stream(Tool.values()).filter(tool->tool!=Tool.UNDECIDED).collect(toList());
		if(runningTools.size() == records.size()) {
			return calculateSummary(new ArrayList<ToolRecord>(records.values()));
		}
		
		for(Tool tool :runningTools) {
			if(records.containsKey(tool))
				continue;
			
			ToolRecord record = new ToolRecord();
			record.setTool(tool);
			ToolRecordContentConverter.initContent(record);
			tDAO.insertToolRecord(record);
		}
		
		return calculateSummary(tDAO.selectAllToolRecords());
	}

	private final static String PPTX_SUFFIX = ".pptx";
	
	public synchronized void addToolRecordSucOnce(Tool tool) throws SMException {
		ToolRecord record = tDAO.selectToolRecordByTool(tool);
		ToolRecordContentConverter.addRecordSucOnce(record);
		tDAO.updateExistedToolRecord(record);
	}
	
	public synchronized void addToolRecordFailOnce(Tool tool) throws SMException {
		ToolRecord record = tDAO.selectToolRecordByTool(tool);
		ToolRecordContentConverter.addRecordFailOnce(record);
		tDAO.updateExistedToolRecord(record);
	}
	
	
	
	@Override
	public byte[] extractPPTImgs(SingleFileUnit file) throws LogicException {
		if(FileUtil.isZip(file.fileName)) {
			try(ByteArrayOutputStream rltStream = new ByteArrayOutputStream();
					BufferedOutputStream rltStreamBuf = new BufferedOutputStream(rltStream,5000);){
				try(ZipOutputStream zipOut = new ZipOutputStream(rltStreamBuf);) {
					try(ZipInputStream inZip = 
							new ZipInputStream(new BufferedInputStream(new ByteArrayInputStream(file.data),5000),
									FileUtil.ZIP_CHARSET);){
						ZipEntry entryInZip;
						while ((entryInZip = inZip.getNextEntry()) != null) {
							if(!entryInZip.getName().endsWith(PPTX_SUFFIX)) {
								continue;
							}
							String folderName = FileUtil.getFileName(entryInZip.getName());
							Map<String,byte[]> imgs =  POIUtil.extractAllImgesFromPPT(inZip);
							for(String zipEntryName:imgs.keySet()) {
								FileUtil.addEntryByBytesWithBuf(imgs.get(zipEntryName), folderName+"/"+zipEntryName, zipOut);
							}
						}
					}
				}
				return rltStream.toByteArray();
			} catch (IOException e) {
				throw new LogicException(SMError.EXTRACT_PPT_FROM_IMAGE_ERROR,"IOError");
			}
		}
		
		if(!file.fileName.endsWith(PPTX_SUFFIX)) {
			throw new LogicException(SMError.EXTRACT_PPT_FROM_IMAGE_ERROR,"文件类型错误 "+FileUtil.getSuffix(file.fileName));
		}
		
		try(InputStream in =new BufferedInputStream(new ByteArrayInputStream(file.data),5000) ;){
			Map<String,byte[]> imgs =  POIUtil.extractAllImgesFromPPT(in);
			try(ByteArrayOutputStream rltStream = new ByteArrayOutputStream();
					BufferedOutputStream rltStreamBuf = new BufferedOutputStream(rltStream,5000);){
				try(ZipOutputStream zipOut = new ZipOutputStream(rltStreamBuf, FileUtil.ZIP_CHARSET);) {
					for(String zipEntryName:imgs.keySet()) {
						FileUtil.addEntryByBytesWithBuf(imgs.get(zipEntryName), zipEntryName, zipOut);
					};
				}
				return rltStream.toByteArray();
			}
		} catch (IOException e) {
			throw new LogicException(SMError.EXTRACT_PPT_FROM_IMAGE_ERROR,"IOError");
		}
	}

	final private JepConfig jepConfig = createPythonConfig();
	
	
	@Override
	public ToolRecordProxy loadToolRecord(Tool tool) throws SMException {
		ToolRecordProxy proxy = new ToolRecordProxy(tDAO.selectToolRecordByTool(tool));
		fill(proxy);
		return proxy;
	}
	
	
	
	private JepConfig createPythonConfig() {
		JepConfig config = new JepConfig();
		config.addIncludePaths("src/main/python/tool");
		return config;
	}
	
	private final File FOLDER_FOR_MODIFY_DPI = new File(SM.SM_EXTERNAL_FILES_DIRECTORY,"imgs_dpi_modifer");
	
	
	@Override
	public byte[] modifyImgsDPI(SingleFileUnit file, int horizontalInch, int verticalInch) throws SMException {
		if(!FOLDER_FOR_MODIFY_DPI.exists()) {
			FOLDER_FOR_MODIFY_DPI.mkdir();
		}
		
		File fileForThis = null;
		while(fileForThis == null) {
			long mark = System.currentTimeMillis(); 
			if(!new File(FOLDER_FOR_MODIFY_DPI,mark+"").exists()) {
				fileForThis = new File(FOLDER_FOR_MODIFY_DPI,String.valueOf(mark));
				fileForThis.mkdir();
			}
		}
		
		assert fileForThis.isDirectory();
		assert fileForThis.listFiles().length == 0;
		
		try (SubInterpreter interp = new SubInterpreter(jepConfig)) {
			interp.eval("from imgs_dpi_modifier import *");
			interp.invoke("modifyImgDPI", file.data,300,fileForThis.getAbsolutePath());
		} catch (JepException e) {
			e.printStackTrace();
			throw new LogicException(SMError.MODIFY_DPI_ERROR);
		}
		return null;
	}
}
