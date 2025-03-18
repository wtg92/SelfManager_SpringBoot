package manager.util;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;

import org.w3c.dom.Element;

import manager.exception.LogicException;
import manager.system.SelfXErrors;

public abstract class ImageUtil {

	final private static Logger logger = Logger.getLogger(ImageUtil.class.getName());
	
	public static BufferedImage copyRagionFrom(BufferedImage src,int startX,int startY,int width,int height) {
		BufferedImage rlt = new BufferedImage(width, height, src.getType());
		for(int i = 0 ; i<width;i++) {
			for(int j=0; j<height;j++) {
				rlt.setRGB(i, j, src.getRGB(startX+i, startY+j));
			}
		}
		return rlt;
	}
	
	public static BufferedImage copyFullAndFillColorInRegionFrom(BufferedImage src,int startX,int startY,int width,int height,Color color) {
		BufferedImage rlt = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
		for(int i = 0 ; i<rlt.getWidth();i++) {
			for(int j=0; j<rlt.getHeight();j++) {
				if(i<startX+width && i>startX
						&& j< startY+height && j>startY) {
					rlt.setRGB(i, j, color.getRGB());
				}else {
					rlt.setRGB(i, j, src.getRGB(i, j));
				}
			
			}
		}
		return rlt;
	}
	

	private static void setDPI(IIOMetadata metadata,int dpi) throws IIOInvalidTreeException {
	    double INCH_2_CM = 2.54;

	       // for PMG, it's dots per millimeter
	    double dotsPerMilli = 1.0 * dpi / 10 / INCH_2_CM;

	    IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
	    horiz.setAttribute("value", Double.toString(dotsPerMilli));

	    IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
	    vert.setAttribute("value", Double.toString(dotsPerMilli));

	    IIOMetadataNode dim = new IIOMetadataNode("Dimension");
	    dim.appendChild(horiz);
	    dim.appendChild(vert);

	    IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
	    root.appendChild(dim);

	    metadata.mergeTree("javax_imageio_1.0", root);
	}
	
    public byte[] process(BufferedImage image, int dpi) throws IOException {
        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName("jpeg"); iw.hasNext();) {
            ImageWriter writer = iw.next();
 
            ImageWriteParam writeParams = writer.getDefaultWriteParam();
            writeParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            //调整图片质量
            writeParams.setCompressionQuality(1f);
 
            IIOMetadata data = writer.getDefaultImageMetadata(new ImageTypeSpecifier(image), writeParams);
            Element tree = (Element) data.getAsTree("javax_imageio_jpeg_image_1.0");
            Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
            jfif.setAttribute("Xdensity", dpi + "");
            jfif.setAttribute("Ydensity", dpi + "");
            jfif.setAttribute("resUnits", "1"); // density is dots per inch
 
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageOutputStream stream = null;
            try {
                stream = ImageIO.createImageOutputStream(out);
                writer.setOutput(stream);
                writer.write(data, new IIOImage(image, null, null), writeParams);
            } finally {
                stream.close();
            }
 
            return out.toByteArray();
        }
        return null;
 
    }
	
	
	
	public static BufferedImage changeDPIOfJPEG(BufferedImage src, int dpi,final String formatName) throws IOException, LogicException {
		
		Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(formatName);
		if (!iw.hasNext()) {
			throw new LogicException(SelfXErrors.IMG_ERROR, "错误的类型" + formatName);

		}
		ImageWriter writer = iw.next();
		ImageWriteParam writeParam = writer.getDefaultWriteParam();
		ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
		IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
		if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
			logger.log(Level.WARNING,"不能转化的图片类型？？");
			assert false;
			throw new RuntimeException("Unknown Type");
		}
		setDPI(metadata, dpi);
		try (ByteArrayOutputStream out = new ByteArrayOutputStream();
				BufferedOutputStream bufOut = new BufferedOutputStream(out);
				ImageOutputStream stream = ImageIO.createImageOutputStream(bufOut)) {
			writer.setOutput(stream);
			writer.write(metadata, new IIOImage(src, null, metadata), writeParam);

			try (InputStream in = new BufferedInputStream(new ByteArrayInputStream(out.toByteArray()))) {
				return ImageIO.read(in);
			}
		}

	}
	
	
	
    
    
    
	/*TODO 这个需求不对 JPG 和 PNG 色彩范围有不同 如果到非公有范围 就会报错*/
	public static List<BufferedImage> unifiedFormat(List<BufferedImage> src,String formatType) throws IOException{
		
		ArrayList<BufferedImage> rlt = new ArrayList<BufferedImage>();
		
		for(BufferedImage img:src) {
	        assert img != null;
			try(ByteArrayOutputStream outOfOneImg = new ByteArrayOutputStream();
					BufferedOutputStream bufferOut = new BufferedOutputStream(outOfOneImg);){
	           ImageIO.write(img, formatType, bufferOut);
	           
	           try(InputStream in = new BufferedInputStream(new ByteArrayInputStream(outOfOneImg.toByteArray()))){
	        	   rlt.add(ImageIO.read(in));
	           }
	        }
		}
		
		disposeBufferedImage(src);
		return rlt;
	}
	
	
	public static void disposeBufferedImage(List<BufferedImage> imgs) {
		disposeBufferedImage(imgs.toArray(new BufferedImage[0]));
	}
	
	public static void disposeBufferedImage(BufferedImage ...imgs) {
		try{
			for(BufferedImage img:imgs) {
				if(img.getGraphics() != null)
					img.getGraphics().dispose();
			}
		}catch(Exception e) {
			logger.log(Level.SEVERE,"disposeBufferedImage error:\n"+e.getMessage());
		}
	}
	
	/*按比例缩放至需要大小*/
	public static BufferedImage changeImgSizeTo(BufferedImage bufImg, int w, int h) {
		Image Itemp = bufImg.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
		double wr = w * 1.0 / bufImg.getWidth(); 
		double hr = h * 1.0 / bufImg.getHeight();

		AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
		Itemp = ato.filter(bufImg, null);
		return (BufferedImage) Itemp;
	}
	
	/*按比例缩放至需要至宽度大小*/
	public static BufferedImage changeImgSizeToByWidth(BufferedImage bufImg, int w) {
		double wr = w * 1.0 / bufImg.getWidth(); 
		int h = (int) (bufImg.getHeight() * wr);
		Image Itemp = bufImg.getScaledInstance(w, h, BufferedImage.SCALE_SMOOTH);
		AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, wr), null);
		Itemp = ato.filter(bufImg, null);
		return (BufferedImage) Itemp;
	}
}
