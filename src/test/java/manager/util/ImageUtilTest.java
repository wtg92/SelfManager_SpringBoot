package manager.util;

import static manager.util.ImageUtil.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

public class ImageUtilTest {
	
	@Test
	public void testCopy() throws Exception {
		File file1 = new File("D:\\sm_files\\yzm","p1.jpg");
		BufferedImage img = ImageIO.read(file1);
		BufferedImage rlt =  copyRagionFrom(img, 10, 10, 200, 200);
		ImageIO.write(rlt, "jpg", new File("D:\\test.jpg"));
	}
	
}
