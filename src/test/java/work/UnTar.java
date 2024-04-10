package work;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import java.util.zip.GZIPInputStream;

public class UnTar {

	public static void main(String[] args) {
		String sourceFolderPath = "C:/temp";
		String tarGzFileName = "OMASWEBI.20240101000001.tar";

		try {
			extractTarGz(tarGzFileName, sourceFolderPath, 1);
			System.out.println("Tar.gz extract  successfully.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void extractTarGz(String tarGzFileName, String outputDirectory, int flg) throws IOException {
		String tarFile = outputDirectory + File.separator + tarGzFileName;

		try (InputStream fileInputStream = new FileInputStream(tarFile);
				TarArchiveInputStream tarInputStream = new TarArchiveInputStream(fileInputStream)) {

			Path outputPath = Paths.get(outputDirectory);

			// Ensure the output directory exists
			if (!Files.exists(outputPath)) {
				Files.createDirectories(outputPath);
			}

			// Extract each entry in the tar archive
			org.apache.commons.compress.archivers.tar.TarArchiveEntry entry;
			while ((entry = tarInputStream.getNextTarEntry()) != null) {
				if (entry.isDirectory()) {
					continue;
				}

				System.out.println("file:" + entry.getName());
				String fileName = entry.getName();
				File file = outputPath.resolve(fileName).toFile();

				try (OutputStream outputFileStream = new FileOutputStream(file)) {
					byte[] buffer = new byte[1024];
					int len;
					while ((len = tarInputStream.read(buffer)) != -1) {
						outputFileStream.write(buffer, 0, len);
					}
				}

				// Extract gz file
				extractGz(outputPath, fileName, flg);
			}
		}
	}

	private static void extractGz(Path outputPath, String fileName, int flg) throws IOException {
		String outFileName;
		String[] names = fileName.split("\\.");

		if (flg == 1) {
			outFileName = names[0] + "_before" + "." + names[1];
		} else {
			outFileName = names[0] + "." + names[1];
		}

		Path filePath = outputPath.resolve(fileName);
		Path outFilePath = outputPath.resolve(outFileName);

		try (FileInputStream fileInput = new FileInputStream(filePath.toFile());
				GZIPInputStream gzipInput = new GZIPInputStream(fileInput);
				FileOutputStream fileOutput = new FileOutputStream(outFilePath.toFile())) {
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = gzipInput.read(buffer)) > 0) {
				fileOutput.write(buffer, 0, len);
			}
		}

		Files.delete(filePath);
	}
}