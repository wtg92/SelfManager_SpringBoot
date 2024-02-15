package manager.util;

import static java.util.stream.Collectors.toList;
import static manager.system.SMParm.USER_TOKEN;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



import com.alibaba.fastjson2.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import manager.exception.SMException;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletRequestContext;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;

import manager.data.SingleFileUnit;
import manager.exception.LogicException;
import manager.exception.NoSuchElement;
import manager.servlet.ServletAdapter;
import manager.system.SMError;

public abstract class UIUtil {

	public static List<Integer> transferToIntList(JSONArray jsonArray) {
		List<Integer> rlt = new ArrayList<>();

		for(int i=0;i<jsonArray.size();i++){
			rlt.add(jsonArray.getInteger(i));
		}

		return rlt;
	}


	/**
	 *  New version migration....
	 */
	public static long getLoginId(String auth) throws LogicException {
		try{
			return ServletAdapter.getUserId(auth.replace("Bearer ",""));
		}catch(Exception e){
			throw new LogicException(SMError.LOGIN_FAILED);
		}
	}

	public static int getParamIntegerOrZeroDefault(JSONObject param, String key){
		Integer val = param.getInteger(key);
		if(val == null){
			return 0;
		}
		return val;
	}

	public static double getParamDoubleOrZeroDefault(JSONObject param, String key){
		Double val = param.getDouble(key);
		if(val == null){
			return 0;
		}
		return val;
	}

	/**
	 *
	 */


	public static String getParam(HttpServletRequest request, String key) throws NoSuchElement {
		String value = request.getParameter(key);
		if(value == null)
			throw new NoSuchElement();
		
		value = value.strip();
		return value;
	}
	
	public static List<String> getParams(HttpServletRequest request, String key) throws NoSuchElement {
		String[] value = request.getParameterValues(key);
		if(value == null)
			throw new NoSuchElement();
		
		return Arrays.stream(value).map(val->val.strip()).collect(toList());
	}
	
	
	
	public static int getNonNullParamInInt(HttpServletRequest request, String key) throws LogicException {
		try {
			return Integer.parseInt(getNonNullParam(request, key));
		} catch (NumberFormatException e) {
			throw new LogicException(SMError.REQUEST_ARG_ILLEGAL,"String int 转化失败 "+key);
		}
	}
	
	public static double getNonNullParamInDouble(HttpServletRequest request, String key) throws LogicException {
		try {
			return Double.parseDouble(getNonNullParam(request, key));
		} catch (NumberFormatException e) {
			throw new LogicException(SMError.REQUEST_ARG_ILLEGAL,"String double 转化失败 "+key);
		}
	}
	
	
	 /**
     * @param   fileName
     * @param   request
     * @Description: 导出文件转换文件名称编码
     */
    public static String encodeFileName(String fileName, HttpServletRequest request) {
        String codedFilename = null;
        try {
            String agent = request.getHeader("USER-AGENT");
            if (null != agent && -1 != agent.indexOf("MSIE") || null != agent && -1 != agent.indexOf("Trident") || null != agent && -1 != agent.indexOf("Edge")) {// ie浏览器及Edge浏览器
                String name = java.net.URLEncoder.encode(fileName, "UTF-8");
                codedFilename = name;
            } else if (null != agent && -1 != agent.indexOf("Mozilla")) {
                // 火狐,Chrome等浏览器
                codedFilename = new String(fileName.getBytes("UTF-8"), "iso-8859-1");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return codedFilename;
    }

	
	
	public static boolean getNonNullParamInBool(HttpServletRequest request, String key) throws LogicException {
		try {
			return Boolean.parseBoolean(getNonNullParam(request, key));
		} catch (Exception e) {
			throw new LogicException(SMError.REQUEST_ARG_ILLEGAL,"String boolean 转化失败 "+key);
		}
	}

	@Deprecated
	public static Calendar getNonNullParamInDate(HttpServletRequest request, String key) throws LogicException {
		try {
			return TimeUtil.parseDate(Long.parseLong(getParam(request, key)));
		} catch (Exception e) {
			e.printStackTrace();
			//TODO TEMP
			try{
				return TimeUtil.parseDate(getParam(request,key));
			}catch(Exception e2){
				throw new LogicException(SMError.REQUEST_ARG_ILLEGAL,"String date 转化失败 "+key);
			}
		}
	}
	
	public static Calendar getNonNullParamInTime(HttpServletRequest request, String key) throws LogicException {
		try {
			return TimeUtil.parseTime(Long.parseLong(getParam(request, key)));
		} catch (Exception e) {
			e.printStackTrace();
			try{
				return TimeUtil.parseTime(getParam(request,key));
			}catch(Exception e2){
				throw new LogicException(SMError.REQUEST_ARG_ILLEGAL,"String date 转化失败 "+key);
			}
		}
	}

	public static String getParamOrBlankDefault(HttpServletRequest request, String key) throws LogicException{
		try {
			return getParam(request, key);
		} catch (NoSuchElement e) {
			return "";
		}
	}
	
	public static<T> List<T> getJSONArrayParam(HttpServletRequest request, String key,Class<T> cla)throws LogicException{
		try {
			String json = getParam(request, key);
			
			List<T> rlt = new ArrayList<T>();
			
			JSONArray arr = JSON.parseArray(json);
			for(int i=0;i<arr.size();i++) {
				rlt.add(arr.getObject(i, cla));
			}
			return rlt;
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.REQUEST_ARG_ILLEGAL,"缺失参数");
		}
	}
	
	
	public static List<Calendar> getParamsOrBlankDefaultInTime(HttpServletRequest request, String key) throws LogicException{
		try {
			return getParams(request, key).stream().map(str->str==null?TimeUtil.getBlank():TimeUtil.parseTime(str)).collect(toList());
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.REQUEST_ARG_ILLEGAL,"缺失参数");
		}
	}
	
	public static List<Boolean> getNonNullParamsInBool(HttpServletRequest request, String key) throws LogicException{
		try {
			return getParams(request, key).stream().map(str->Boolean.parseBoolean(str)).collect(toList());
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.REQUEST_ARG_ILLEGAL,"缺失参数");
		}
	}
	
	
	public static List<String> getParamsOrBlankDefault(HttpServletRequest request, String key) throws LogicException{
		try {
			return getParams(request, key).stream().map(str->str == null ? "" : str).collect(toList());
		} catch (NoSuchElement e) {
			return Collections.emptyList();
		}
	}

	public static Map<String,Object> generateResObj(Object ...params){
    	Map<String,Object> rlt = new HashMap<>();
    	for(int i=0;i<params.length;i++){
    		rlt.put("res"+i,params[i]);
		}
    	return rlt;
	}

	public static int getParamIntegerOrZeroDefault(HttpServletRequest request, String key) throws LogicException{
		try {
			String val = getParam(request, key);
			if(val.length() == 0)
				return 0;
			
			return Integer.parseInt(val);
		} catch (NoSuchElement e) {
			return 0;
		}
	}
	
	public static int getParamOrZeroDefaultInInt(HttpServletRequest request, String key) throws LogicException{
		try {
			String val = getParam(request, key);
			if(val.length() == 0)
				return 0;
			
			return Integer.parseInt(val);
		} catch (NoSuchElement e) {
			return 0;
		}
	}
	
	public static double getParamOrZeroDefaultInDouble(HttpServletRequest request, String key) throws LogicException{
		try {
			String val = getParam(request, key);
			if(val.length() == 0)
				return 0;
			
			return Double.parseDouble(val);
		} catch (NoSuchElement e) {
			return 0;
		}
	}
	
	public static boolean getParamOrFalseDefault(HttpServletRequest request, String key) throws LogicException{
		try {
			return Boolean.parseBoolean(getParam(request, key));
		} catch (NoSuchElement e) {
			return false;
		}
	}
	
	public static Calendar getParamOrBlankDefaultInDate(HttpServletRequest request, String key) throws LogicException{
		try {
			String val = getParam(request, key);
			if(val.length() == 0)
				return TimeUtil.getBlank();
			
			return TimeUtil.parseDate(val);
		} catch (NoSuchElement e) {
			return TimeUtil.getBlank();
		}
	}
	
	public static Calendar getParamOrBlankDefaultInTime(HttpServletRequest request, String key) throws LogicException{
		try {
			String val = getParam(request, key);
			if(val.length() == 0)
				return TimeUtil.getBlank();
			
			return TimeUtil.parseTime(val);
		} catch (NoSuchElement e) {
			return TimeUtil.getBlank();
		}
	}
	
	public static String getNonNullParam(HttpServletRequest request, String key) throws LogicException{
		try {
			return getParam(request, key);
		} catch (NoSuchElement e) {
			e.printStackTrace();
			throw new LogicException(SMError.REQUEST_ARG_NULL,key);
		}
	}
	
	public static List<String> getParamsOrEmptyList(HttpServletRequest request, String key) throws LogicException{
		try {
			return getParams(request, key);
		} catch (NoSuchElement e) {
			return Collections.emptyList();
		}
	}
	
	public static List<String> getNonNullParams(HttpServletRequest request, String key) throws LogicException{
		try {
			return getParams(request, key);
		} catch (NoSuchElement e) {
			throw new LogicException(SMError.REQUEST_ARG_NULL,key);
		}
	}
	
	public static List<Integer> getNonNullParamsInInt(HttpServletRequest request, String key) throws LogicException{
		try {
			return getParams(request, key).stream().map(v->Integer.parseInt(v)).collect(toList());
		} catch (NoSuchElement e) {
			return new ArrayList<Integer>();
		}
	}
	
	public static List<Integer> getParamsInIntOrZeroDefault(HttpServletRequest request, String key) throws LogicException{
		try {
			return getParams(request, key).stream().map(v->v== null ? 0 : Integer.parseInt(v)).collect(toList());
		} catch (NoSuchElement e) {
			return new ArrayList<>();
		}
	}
	
	
	public static String getNullObjJSON() {
		return "{}";
	}


	

	public static String getParamJSON(Object arg) {
		Map<String,Object> rlt = new HashMap<>();
		rlt.put("rlt", arg);
		return JSON.toJSONString(rlt);
	}
	
	public static String getBiParamsJSON(Object arg1,Object arg2) {
		Map<String,Object> rlt = new HashMap<>();
		rlt.put("firstRlt", arg1);
		rlt.put("secondRlt", arg2);
		return JSON.toJSONString(rlt);
	}



	public static long getLoginId(HttpServletRequest request) throws LogicException {
		String token = getNonNullParam(request,USER_TOKEN); 
		return ServletAdapter.getUserId(token);
	}
	
	private final static double MAX_SIZE_OF_MB_FOR_SINGLE_FILE = 20;
//
//	public static SingleFileUnit parseSingleFile(HttpServletRequest request) throws LogicException{
//		DiskFileItemFactory factory = new DiskFileItemFactory();
//		ServletFileUpload uploader = new ServletFileUpload(factory);
//		List<FileItem> items = null;
//		try {
//			items = uploader.parseRequest(new ServletRequestContext(request)).stream()
//					.filter(item->!item.isFormField()).collect(toList());
//		} catch (FileUploadException e) {
//			e.printStackTrace();
//			throw new LogicException(SMError.FIEL_UPLOADING_ERROR,"解析失败");
//		}
//		if(items.size() == 0) {
//			throw new LogicException(SMError.FIEL_UPLOADING_ERROR,"上传文件失败");
//		}
//		if(items.size()>1) {
//			throw new LogicException(SMError.FIEL_UPLOADING_ERROR,"一次上传了多个文件 "+items.size());
//		}
//
//		FileItem item = items.get(0);
//		if(FileUtil.getMBBySize(item.getSize())>MAX_SIZE_OF_MB_FOR_SINGLE_FILE) {
//			throw new LogicException(SMError.FIEL_UPLOADING_ERROR,"文件大小超过"+MAX_SIZE_OF_MB_FOR_SINGLE_FILE+"MB的限制 文件大小为"+String.format("%.2f", FileUtil.getMBBySize(item.getSize()))+"MB");
//		};
//
//		SingleFileUnit unit = new SingleFileUnit();
//		unit.fileName = item.getName();
//		unit.data = item.get();
//
//		return unit;
//	}


}
