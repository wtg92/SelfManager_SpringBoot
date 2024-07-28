package manager.logic.tool;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

import com.alibaba.fastjson2.JSON;

import manager.data.SingleFileUnit;
import manager.data.tool.ToolRecordSummary;
import manager.exception.SMException;
import manager.system.tool.Tool;
import manager.cache.CacheUtil_OLD;
import manager.util.FileUtil;

public class DEBUG_ToolLogic {
	
	
	
	@Test
	public void debug() throws SMException {
		CacheUtil_OLD.clearAllCache_ONLYFORTEST();
		ToolLogic tL = ToolLogic.getInstance();
		ToolRecordSummary summary = tL.loadToolRecordSummary(1);
		assert summary.records.get(0).content != null;
		assert !summary.records.get(0).content.version.equals("");
	}
	
	@Test
	public void debug2()throws SMException {
		ToolLogic tL = ToolLogic.getInstance();
		tL.addToolRecordSucOnce(Tool.IMGS_EXTRACTOR_OF_PPT);
	}
	
	/*fastJSON有BUG List嵌套的泛型竟然无法映射全属性*/
	@Test
	public void testJSON() {
		String str = "{\"records\":[{\"content\":{\"failCount\":0,\"sucCount\":0,\"version\":\"0.1\"},\"record\":{\"content\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n<rec version=\\\"0.1\\\" suc_count=\\\"0\\\" fail_count=\\\"0\\\" tool=\\\"1\\\"><his/></rec>\",\"createTime\":1610878639000,\"hbVersion\":0,\"id\":1,\"tool\":\"IMGS_EXTRACTOR_OF_PPT\",\"updateTime\":1610878639000}},{\"content\":{\"failCount\":0,\"sucCount\":0,\"version\":\"0.1\"},\"record\":{\"content\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\"?>\\n<rec version=\\\"0.1\\\" suc_count=\\\"0\\\" fail_count=\\\"0\\\" tool=\\\"2\\\"><his/></rec>\",\"createTime\":1610878639000,\"hbVersion\":0,\"id\":2,\"tool\":\"IMGS_DPI_MODIFER\",\"updateTime\":1610878639000}}]}";
		ToolRecordSummary summary = JSON.parseObject(str, ToolRecordSummary.class);
		assert summary.records.size() == 2;
		assert summary.records.get(0).content.failCount == 0;
	}
	
	
	@Test
	public void testPython() throws Exception {
		ToolLogic.testPython_ONLYFORTEST();
	}
	
	
	@Test
	public void testDPIModifier() throws Exception {
		ToolLogic tL =  ToolLogic.getInstance();
		File srcImg = new File("D:\\GoogleDownloads\\中期汇报 (11)","hdphoto1.wdp");
		assert srcImg.isFile();
		try(ByteArrayOutputStream out = new ByteArrayOutputStream();
				InputStream in = new FileInputStream(srcImg)){
			FileUtil.copyWithoutBuffer(in, out);
			byte[] srcBytes = out.toByteArray();
			System.out.println(srcBytes.length);
			SingleFileUnit single = new SingleFileUnit();
			single.data = srcBytes;
			tL.modifyImgsDPI(single, 300, 300);
		}
	}
}
