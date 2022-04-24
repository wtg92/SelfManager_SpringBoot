package work;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.junit.Test;

public class DEBUG_SplitBigTxt {
	
	@Test
	public void test() throws IOException {
		File src = new File("D:\\GoogleDownloads","stdout.log");
		assert src.isFile();
		File targetDir = new File("D:\\Work\\2022.3.24\\日志");
		assert targetDir.isDirectory();
		
		
		try(Reader reader = new BufferedReader(new FileReader(src),10000)){
			int count = 0;
			char[] buf = new char[1024];
			int b = 0;
			int fileCounter = 0;
			Writer writer = null;
			while(b!=-1) {
				b = reader.read(buf);
				if(writer == null) {
					count ++ ;
					File dest = new File(targetDir,count+".txt");
					dest.createNewFile();
					writer = new BufferedWriter(new FileWriter(dest),10000);
				}
				writer.write(buf);
				fileCounter++;
				
				if(fileCounter > 10000) {
					writer.close();
					writer = null;
					fileCounter = 0;
				}
				
			}
			
		}
	}
	
}
