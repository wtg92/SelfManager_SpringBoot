package manager.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.Test;



public class DEBUG_POI {
	
	@Test
	public void testCommon() {
		Integer.parseInt(" ");
	}
	
	@Test
	public void testPPT() throws Exception{
		final File src = new File("D:\\Work","中期汇报.pptx");
		assert src.exists();
		Map<String,byte[]> images =  POIUtil.extractAllImgesFromPPT(src);
		File rltFolder = new File("D:\\Work\\img");
		FileUtil.clearDirectory(rltFolder);
		images.forEach((name,img)->{
			try {
				FileUtil.copyBytesToFile(img, new File(rltFolder,name));
			} catch (IOException e) {
				e.printStackTrace();
				assert false;
			}
		});
	}
	
	@Test
	public void changeOneImg() {
		
		
		
	}
	
	@Test
	public void testWord() throws Exception{
		final File word = new File("D:\\Work","法信工程平台需求明细（3月12日）(1).docx");
		assert word.exists();
		try (InputStream in = new BufferedInputStream(new FileInputStream(word),10000);
				XWPFDocument doc = new XWPFDocument(in);
				XWPFWordExtractor ex = new XWPFWordExtractor(doc);){
			System.out.println(ex.getText());
		}
	}
	
}
