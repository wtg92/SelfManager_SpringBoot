package manager.util;

import static java.util.stream.Collectors.toList;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import com.alibaba.fastjson2.annotation.JSONField;

import com.fasterxml.jackson.annotation.JsonIgnore;
import manager.exception.LogicException;
import manager.system.SelfXErrors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *   验证码图片要求必须是jpg
 * TODO 简单实现了一下验证码，太烦了，未来再改进吧，现在能用，但是有点丑
 * 
 * @author 王天戈
 *
 */
@Component
public  class YZMUtil implements Serializable {
	@Value("${file.external.root}")
	private String externalDir;

	private final static double YZM_MAX_RATIO = 0.45;
	private final static double YZM_MIN_RATIO = 0.25;
	/* 距离图片边缘最小值 */
	private final static double DISTANCE_EDGE_RATIO = 0.1;

	public static class YZMInfo implements Serializable {
		@JSONField(serialize = false)
		@JsonIgnore
		public int xForCheck;
		
		public String srcImg;
		public int yOffset;
		
		@JSONField(serialize = false)
		@JsonIgnore
		public BufferedImage cutImg;
		@JSONField(serialize = false)
		@JsonIgnore
		public BufferedImage backgroundImg;
		
		/*For UI*/
		public int widthForSrc;
		public int heightForSrc;
		public String cutImgBase64;
		public String backgroundImgBase64;
		public boolean checkSuccess = false;
	}

	public YZMInfo createYZM(String alreadyUsedImg) throws LogicException {
		try {
			YZMInfo rlt = new YZMInfo();
			final List<File> noDupImgs = Arrays.stream(new File(externalDir, "yzm").listFiles())
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
			throw new LogicException(SelfXErrors.ERROR_CREATE_YZM);
		}
	}

}
