package manager.util;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.sl.usermodel.SlideShowFactory;
import org.apache.poi.xslf.usermodel.XSLFPictureData;

import manager.exception.LogicException;
import manager.system.SMError;

public class POIUtil {
	
	/**
	 * 
	 * @param src
	 * @return  key  picture 名
	 * @throws LogicException
	 */
	public static Map<String,byte[]> extractAllImgesFromPPT(File src) throws LogicException {
		assert src.exists();
		try(InputStream in = new BufferedInputStream(new FileInputStream(src))){
			return extractAllImgesFromPPT(in);
		} catch (FileNotFoundException e) {
			throw new LogicException(SMError.PPT_EEROR,"找不到文件");
		} catch (IOException e) {
			e.printStackTrace();
			throw new LogicException(SMError.PPT_EEROR);
		}
	}
	
	
	public static Map<String,byte[]> extractAllImgesFromPPT(InputStream in) throws LogicException {
		Map<String,byte[]> images = new HashedMap<>();
		@SuppressWarnings("rawtypes")
		SlideShow ppt;
		try {
			ppt = SlideShowFactory.create(in);
		} catch (IOException e) {
			e.printStackTrace();
			throw new LogicException(SMError.PPT_EEROR,"IO Error");
		}
		 @SuppressWarnings("unchecked")
		List<XSLFPictureData> pictures = ppt.getPictureData();
		 for(XSLFPictureData pic:pictures) {
			images.put(pic.getFileName(), pic.getData());
		 }
		 return images;
	}
	
	
	
	
}
