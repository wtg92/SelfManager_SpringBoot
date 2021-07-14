package manager.util;

import static java.util.stream.Collectors.toList;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import com.alibaba.fastjson.annotation.JSONField;

import manager.exception.LogicException;
import manager.system.SM;
import manager.system.SMError;

/**
 *   验证码图片要求必须是jpg
 * TODO 简单实现了一下验证码，太烦了，未来再改进吧，现在能用，但是有点丑
 * 
 * @author 王天戈
 *
 */
public abstract class YZMUtil {

	public final static File YZM_SRC_DIRECTORY = new File(SM.SM_EXTERNAL_FILES_DIRECTORY, "yzm");

	private final static double YZM_MAX_RATIO = 0.45;
	private final static double YZM_MIN_RATIO = 0.25;
	/* 距离图片边缘最小值 */
	private final static double DISTANCE_EDGE_RATIO = 0.1;

	public static class YZMInfo {
		@JSONField(serialize = false)
		public int xForCheck;
		
		public String srcImg;
		public int yOffset;
		
		@JSONField(serialize = false)
		public BufferedImage cutImg;
		@JSONField(serialize = false)
		public BufferedImage backgroundImg;
		
		/*For UI*/
		public int widthForSrc;
		public int heightForSrc;
		public String cutImgBase64;
		public String backgroundImgBase64;
		public boolean checkSeccuss = false;
	}

	public static YZMInfo createYZM(String alreadyUsedImg) throws LogicException {
		try {
			YZMInfo rlt = new YZMInfo();
			final List<File> noDupImgs = Arrays.stream(YZM_SRC_DIRECTORY.listFiles())
					.filter(file -> file.getName().endsWith(".jpg") && !file.getName().equals(alreadyUsedImg))
					.collect(toList());

			if (noDupImgs.size() == 0)
				throw new RuntimeException("验证码图片严重不足（至少需要两张）");

			int randomIndex = CommonUtil.getByRandom(0, noDupImgs.size());
			assert randomIndex < noDupImgs.size();
			final File base = noDupImgs.get(randomIndex);

			rlt.srcImg = base.getName();

			BufferedImage baseImg = ImageIO.read(base);
			int width = baseImg.getWidth();
			int height = baseImg.getHeight();
			
			rlt.widthForSrc = width;
			rlt.heightForSrc = height;
			
			int widthForCut = CommonUtil.getByRandom((int) (width * YZM_MIN_RATIO), (int) (width * YZM_MAX_RATIO));
			int heightForCut = CommonUtil.getByRandom((int) (height * YZM_MIN_RATIO), (int) (height * YZM_MAX_RATIO));

			int startX = CommonUtil.getByRandom((int) (DISTANCE_EDGE_RATIO * width),
					(int) (width * (1 - DISTANCE_EDGE_RATIO)) - widthForCut);
			int startY = CommonUtil.getByRandom((int) (DISTANCE_EDGE_RATIO * height),
					(int) (height * (1 - DISTANCE_EDGE_RATIO)) - heightForCut);
			rlt.xForCheck = startX;
			rlt.yOffset = startY;
			BufferedImage dragImg = ImageUtil.copyRagionFrom(baseImg, startX, startY, widthForCut, heightForCut);
			rlt.backgroundImg = ImageUtil.copyFullAndFillColorInRegionFrom(baseImg, startX, startY, widthForCut,
					heightForCut, Color.CYAN);
			rlt.cutImg = dragImg;
			return rlt;
		} catch (Exception e) {
			e.printStackTrace();
			throw new LogicException(SMError.ERROR_CREATE_YZM);
		}
	}

}
