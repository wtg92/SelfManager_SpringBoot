package manager.logic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;


import manager.system.SM;
import manager.util.ImageUtil;
import manager.util.YZMUtil;

public class DEBUG_RefineYZMSize {
	
	
	
	/**
	  *  让验证码图片的宽度都是478像素
	  * 和前台Dialog保持一致
	 * @throws IOException
	 */
	@Test
	public void test() throws IOException {
		
		final int DEFAULT_WIDTH = 478;
		
		for(File file:YZMUtil.YZM_SRC_DIRECTORY.listFiles((file)->file.getName().endsWith(".jpg"))) {
			BufferedImage src = ImageIO.read(file);
			if(src.getWidth() == DEFAULT_WIDTH)
				continue;
			
			BufferedImage theStandard = ImageUtil.changeImgSizeToByWidth(src, DEFAULT_WIDTH);
			file.delete();
			
			ImageIO.write(theStandard, "jpg", new File(YZMUtil.YZM_SRC_DIRECTORY,file.getName()));
		}
	}
	
}
