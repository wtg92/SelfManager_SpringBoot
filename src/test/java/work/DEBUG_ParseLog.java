package work;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class DEBUG_ParseLog {
	
	private static final String EXTCM_PREFIX= "extcm";
	
	private static final String ESTACK_FILE_NAME = "estack.log";
	
	private static final List<String> keyMainError = Arrays.asList("null","SQL","index out");
	
	private static final List<String> expendErrorKey = Arrays.asList("无明细信息","异地缴存信息不存在"
			,"剩余本金小于或等于0不能进行计算","用户不是灵活就业人员"
			,"个人基本信息与社保登记信息不符，请确认"
			,"该笔流水已挂账"
			,"该付款账号备案记录非注销状态，无法启用，请确认！"
			,"重复的登记"
			,"密码错误"
			,"个人账户设立回执查询无信息"
			,"贷款账号和状态为必输项"
			,"无"
			,"未"
			,"不"
			,"失败"
			,"请");
	
	public static boolean containsChinese(String str) {
		Pattern p = Pattern.compile("[\u4E00-\u9FA5|\\！|\\，|\\。|\\（|\\）|\\《|\\》|\\“|\\”|\\？|\\：|\\；|\\【|\\】]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}
	
	
	@Test
	public void testExtackLog() throws IOException {
		final File parentFolder = new File("D:\\Work\\2021.5.18\\2019-09-06");
		
		final StringBuffer report = new StringBuffer();
		
		assert parentFolder.isDirectory();
		
		report.append("2019-09-06 生产日志");
		report.append("\n");
		
		List<String> noErrorFolder = new ArrayList<String>();
		List<EstackLogInfo> errorInfos = new ArrayList<>();
		for(File subFolder : parentFolder.listFiles()) {
			
			if(!subFolder.isDirectory())
				continue;
				
			List<File> targetFiles = Files.list(Path.of(subFolder.getAbsolutePath())).map(Path::toFile)
					.filter(f -> f.getName().contains(ESTACK_FILE_NAME))
					.collect(toList());
			
			if(targetFiles.size() == 0 ) {
				noErrorFolder.add(subFolder.getName());
				continue;
			}
			
			assert targetFiles.size() == 1;
			
			final File targetErrorFile = targetFiles.get(0);
			try {
				EstackLogInfo info = parseEstackLog(targetErrorFile);
				info.folder = subFolder.getName();
				errorInfos.add(info);
			}catch (AssertionError e) {
				System.out.println(subFolder.getName());
				throw e;
			}
	
		}
		
		
		
		report.append(noErrorFolder.stream().limit(3).collect(joining(",")));
		report.append(" 等"+noErrorFolder.size()+"个文件夹 无错误日志");
		report.append("\n");
		
		errorInfos.forEach(info->{
			report.append("\t");
			report.append(info.folder+"异常情况");
			report.append("\n");
			info.errosInfo.forEach((key,exceptions)-> {
//				if(exceptions.stream().noneMatch(str->keyMainError.stream().anyMatch(ss->str.contains(ss)))) {
//					return;
//				}
				if(exceptions.stream().anyMatch(str->expendErrorKey.stream().anyMatch(ss->str.contains(ss)))) {
					return;
				}
//				
//				if(exceptions.stream().anyMatch(str->containsChinese(str))) {
//					return;
//				}
				
				
				report.append("\t\t");
				report.append(key.toLineInfo());
				report.append("\n");
				exceptions.forEach(e->{
					report.append("\t\t\t");
					report.append(e);
					report.append("\n");
				});
			});
			
		});
		
		
		final File storage = new File("D:\\Work\\2021.6.1","demo.txt");
		
		try(BufferedWriter writer = new BufferedWriter(new FileWriter(storage))){
			writer.write(report.toString());
		}
		
		
	}
	
	@Test
	public void testExtcmLog() throws Exception{
		
		final File folder = new File("D:\\Work\\2021.5.18\\2019-09-06\\cert_input");
		assert folder.isDirectory();
		
 		List<ExtcmLogInfo> infos = new ArrayList<>();

		for(File src:folder.listFiles()) {
			if(!src.getName().startsWith(EXTCM_PREFIX)) {
				continue;
			}
			try {
				infos.add(parseExtcmLogInfo(src));
			}catch (Exception e) {
				System.out.println(src.getName());
				throw e;
			}
			
		}
		
		
		List<String> withoutIps  =infos.stream().filter(info->info.withoutIpReqLines.size()>0).flatMap(info->info.withoutIpReqLines.stream()).distinct().collect(toList());
		
		System.out.println("------------不带IP的Line-------种类数量 "+withoutIps.size());
		
		withoutIps.forEach(str->{
//			System.out.println(str);
		});
		
		System.out.println("------------不带IP的Line-------");
		
		infos.stream().limit(5).forEach(info->{
			System.out.println(info.fileName);
			if(info.ips.size() != 1) {
				System.out.println(info.fileName);
			}
		});
		
		System.out.println(infos.size());
	}
	
	
	private EstackLogInfo parseEstackLog(File targetErrorFile) throws IOException {
		EstackLogInfo info = new EstackLogInfo();
		try(BufferedReader  reader = new BufferedReader(new FileReader(targetErrorFile)) ){
			reader.lines().forEach(line->{
				parseAndFill(line,info);
			});
		}
		return info;
	}
	
	
	private static void parseAndFill(String line, EstackLogInfo info) {
		if(line.trim().length() == 0)
			return;
		
		if(isKeyInfo(line)) {
			int timeStartIndex = line.lastIndexOf("[");
			int timeEndIndex = line.lastIndexOf("]");
			KeyLine keyLine = new KeyLine();
			
			keyLine.timeStr = line.substring(timeStartIndex+1,timeEndIndex);
			
			String[] units = line.substring(0, timeStartIndex).trim().split(" ");
			for(String unit:units) {
				if(unit.startsWith("STEP")) {
					keyLine.step = extractInfoFromKeyUnit(unit);
					continue;
				}
				if(unit.startsWith("SEQ")) {
					keyLine.seq = extractInfoFromKeyUnit(unit);
					continue;
				}
				if(unit.startsWith("REQ")) {
					keyLine.req = extractInfoFromKeyUnit(unit);
					continue;
				}
				if(unit.startsWith("TID")) {
					keyLine.TID = extractInfoFromKeyUnit(unit);
					continue;
				}
				System.err.println("未解析的KeyLine:" + unit);
			}
			info.curLine = keyLine;
			return;
		}
		
		assert info.curLine != null;
		
		if(isKeyExceptionInfo(line)) {
			if(!info.errosInfo.containsKey(info.curLine)) {
				info.errosInfo.put(info.curLine, new ArrayList<String>());
			}
			info.errosInfo.get(info.curLine).add(line);
		}
	}


	private static boolean isKeyExceptionInfo(String line) {
//		return !line.startsWith("	");
		return true;
	}


	public static class KeyLine{
		public String step;
		public String seq;
		public String req;
		public String TID;
		public String timeStr;
		
		public String toLineInfo() {
			return String.format("SEQ:[%s] REQ:[%s] TID[%s] Time[%s]", seq,req,TID,timeStr);
		}
		
	}
	
	
	
	
	private static ExtcmLogInfo parseExtcmLogInfo(File src) throws IOException {
		ExtcmLogInfo info = new ExtcmLogInfo();
		info.fileName = src.getName();
		
		try(BufferedReader  reader = new BufferedReader(new FileReader(src)) ){
			reader.lines().forEach(line->{
				if(isKeyInfo(line)) {
					checkAndFillForKeyInfo(line,info);
					return;
				}
				if(isJSONInfo(line)) {
					info.JSONInfoStrs.add(line);
					JSONObject obj = JSON.parseObject(line);
					JSONObject sysHead = obj.getJSONObject("SYS_HEAD");
					assert sysHead != null;
					String ip = sysHead.getString("REQ_IP");
					if(ip == null) {
						info.withoutIpReqLines.add(line);
						return;
					}
					if(!info.ips.contains(ip)) {
						info.ips.add(ip);
					}
					return;
				}
			});
		}
		return info;
	}
	
	
	private static boolean isJSONInfo(String line) {
		if(line.trim().length() == 0) {
			return false;
		}
		
		try {
			JSON.parse(line);
			return true;
		}catch(JSONException e) {
			return false;
		}
	}

	private static String extractInfoFromKeyUnit(String unit) {
		assert unit.endsWith("]") : unit;
		int startIndex = unit.indexOf("[");
		assert startIndex != -1;
		
		return unit.substring(startIndex+1,unit.length()-1);
	}
	
	private static void checkAndFillForKeyInfo(String line,ExtcmLogInfo info) {
		String[] units = line.split(" ");
		
		for(String unit:units) {
			if(unit.startsWith("SEQ")) {
				String seq = extractInfoFromKeyUnit(unit);
				if(info.seqs.contains(seq)) {
					continue;
				}
				info.seqs.add(seq);
				continue;
			}
			
//			if(unit.startsWith("REQ")) {
//				String req = extractInfoFromKeyUnit(unit);
//				if(info.req == null) {
//					info.req = req;
//					continue;
//				}
//				if(!info.req.equals(req)) {
//					throw new RuntimeException("REQ 不一致 "+info.req+" vs "+req);
//				}
//				
//				continue;
//			}
			continue;
		}
		
	}

	private static boolean  isKeyInfo(String line) {
		return line.startsWith("STEP");
	}
	
	public static class EstackLogInfo{
		KeyLine curLine = null;
		String folder = "";
		Map<KeyLine,List<String>> errosInfo = new HashMap<DEBUG_ParseLog.KeyLine, List<String>>();
	}
	
	public static class ExtcmLogInfo{
		public String fileName = "";
		public String req = null;
		public List<String> seqs = new ArrayList<String>();
		public List<String> JSONInfoStrs = new ArrayList<>();
		public List<String> ips =  new ArrayList<String>();
		public List<String> withoutIpReqLines = new ArrayList<String>();
	}
	
	
	
	
}
