package book;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class IOTest {
	
	final static File exam1 = new File("D:\\test","test.docx");
	
	public void testBasic() throws IOException {
		try(FileInputStream in = new FileInputStream(exam1)){
			
		}
		
		String str ="something....";
		byte[] dataForStr = str.getBytes();
		try(ByteArrayInputStream in = new ByteArrayInputStream(dataForStr)){
			in.close();
		}
		
		FileInputStream in = null;
		in.close();
		
	}
	
	
}
