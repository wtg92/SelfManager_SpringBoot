package book;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

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
	

	@Test
	public void testCopyFile() throws Exception{
		final File src = new File("D:\\test","test.docx");
		final File target = new File("D:\\test","target.docx");
		
		try(InputStream in = new FileInputStream(src);
				OutputStream out = new FileOutputStream(target)){
			int b;
			while ((b = in.read()) != -1) {
				out.write(b);
			}
		}
		
		
	}
	
	@Test
	public void testIterator() {
		
		List<Byte> data = new ArrayList<Byte>();
		data.add((byte)1);
		data.add((byte)2);
		
		Iterator<Byte> iterator = data.iterator();
		
		int dataLikeIO;
		while(iterator.hasNext()) {
			dataLikeIO = iterator.next();
		}
		
	}
	
}
