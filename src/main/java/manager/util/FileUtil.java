package manager.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileUtil {

	public final static Charset ZIP_CHARSET = Charset.forName("GBK");

	public static void clearDirectory(File directory) {
		assert directory.isDirectory();
		for (File f : directory.listFiles()) {
			f.delete();
		}
		;
	}
	
	
	public static void copyFileFromBase64(String str,File file) throws IOException {
		byte[] data = Base64.getDecoder().decode(str);
		
		try(InputStream in = new BufferedInputStream(new ByteArrayInputStream(data),10000);
				OutputStream out = new BufferedOutputStream(new FileOutputStream(file),10000);){
			copyWithoutBuffer(in, out);
		}
	}
	
	
	
	
	public static void copyBytesToFile(byte[] data, File file) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		}

		try (InputStream src = new BufferedInputStream(new ByteArrayInputStream(data));
				OutputStream out = new BufferedOutputStream(new FileOutputStream(file));) {
			copyWithoutBuffer(src, out);
		}
	}
	
	public static void copyWithoutBuffer(InputStream in, OutputStream out) throws IOException {
		int b;
		while ((b = in.read()) != -1) {
			out.write(b);
		}
		out.flush();
	}

	public static String getSuffix(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index + 1 > fileName.length() - 1) {
			return "";
		}

		return fileName.substring(index + 1);
	}

	public static String getFileName(String fileName) {
		int index = fileName.lastIndexOf(".");
		if (index == -1) {
			return fileName;
		}
		return fileName.substring(0, index);
	}

	public static boolean isZip(String name) {
		return name.endsWith(".zip");
	}

	public static double getMBBySize(long size) {
		return size / (1024.0 * 1024.0);
	}

	public static int byteArrayToInt(byte[] bytes) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (3 - i) * 8;
			value += (bytes[i] & 0xFF) << shift;
		}
		return value;
	}

	
	public static void addEntryByBytesWithBuf(byte[] src,String entryNameWithPath,ZipOutputStream targetOut) throws IOException {
		targetOut.putNextEntry(new ZipEntry(entryNameWithPath));
		try(InputStream in = new BufferedInputStream(new ByteArrayInputStream(src))){
			copyWithoutBuffer(in, targetOut);
			targetOut.closeEntry();
		}
	}
	
	public static File getOrInitFile(String path) {
		File file = new File(path);
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
				throw new RuntimeException(e1.getMessage());
			}
		}
		return file;
	}

	public static byte[] intToByteArray(int i) {
		byte[] result = new byte[4];
		result[0] = (byte) ((i >> 24) & 0xFF);
		result[1] = (byte) ((i >> 16) & 0xFF);
		result[2] = (byte) ((i >> 8) & 0xFF);
		result[3] = (byte) (i & 0xFF);
		return result;
	}


	private static final long KB_IN_MB = 1024; // 1MB = 1024KB
	private static final long KB_IN_GB = 1024 * 1024; // 1GB = 1024 * 1024 KB

	// ==================== MB <-> KB ====================

	/** MB 转 KB */
	public static long mbToKb(long mb) {
		return mb * KB_IN_MB;
	}

	/** KB 转 MB（取整） */
	public static long kbToMb(long kb) {
		return kb / KB_IN_MB;
	}

	/** KB 转 MB（保留小数） */
	public static double kbToMbDouble(long kb) {
		return (double) kb / KB_IN_MB;
	}

	// ==================== KB <-> GB ====================

	/** KB 转 GB（取整） */
	public static long kbToGb(long kb) {
		return kb / KB_IN_GB;
	}

	/** KB 转 GB（保留小数） */
	public static double kbToGbDouble(long kb) {
		return (double) kb / KB_IN_GB;
	}

	/** GB 转 KB */
	public static long gbToKb(long gb) {
		return gb * KB_IN_GB;
	}

}
