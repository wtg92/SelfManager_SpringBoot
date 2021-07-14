package manager.util;

import org.junit.Test;
import static manager.util.YZMUtil.*;

import java.io.File;

import javax.imageio.ImageIO;

import manager.exception.LogicException;
import manager.system.SM;

public class YZMUtilTest {

	@Test
	public void testBasic() throws Exception {
		assert SM.SM_EXTERNAL_FILES_DIRECTORY.isDirectory();
		YZMInfo info = createYZM("");
		ImageIO.write(info.cutImg, "jpg", new File("D:\\test_cut.jpg"));
		ImageIO.write(info.backgroundImg, "jpg", new File("D:\\test_back.jpg"));
	}
	
}
