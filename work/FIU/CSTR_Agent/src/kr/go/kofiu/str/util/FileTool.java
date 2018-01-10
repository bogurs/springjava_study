package kr.go.kofiu.str.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/*******************************************************
 * <pre>
 * 업무   그룹명  : STR 시스템
 * 서브   업무명  : 보고 기관 Agent
 * 설        명  : 
 * 작   성   자  : 선만주 
 * 작   성   일  : 2008.09.01
 * copyright @ LG CNS. All Right Reserved
 * 
 * <pre>
 *******************************************************/
public class FileTool {
	private FileTool() {}
	
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(FileTool.class
			.getName());

	/**
	 * 
	 * @param source
	 * @param target
	 * @throws FileIOException
	 */
	public static String move(File source, String destination)
			throws IOException {
		return move(source, destination, false);
	}

	/**
	 * 
	 * @param source
	 * @param target
	 * @param overwrite
	 * @throws FileIOException
	 */
	public static String move(File source, String destination, boolean overwrite)
			throws IOException {
		File target = FileTool.getUiqueFileName(destination, overwrite);
		copyFile(source, target);

		if (!source.delete()) {
			if (logger.isLoggable(Level.WARNING))
				logger.warning("파일 삭제에 실패했습니다.");
			// lee debug throw new IOException(source + " is not deleted !");
		}

		return destination;
	}

	/**
	 * copy file
	 * 
	 * @param in
	 * @param out
	 * @throws FileIOException
	 */
	public static void copyFile(File in, File out) throws IOException {
		FileChannel sourceChannel = new FileInputStream(in).getChannel();

		if (!out.exists())
			out.createNewFile();

		FileChannel destinationChannel = new FileOutputStream(out).getChannel();
		sourceChannel.transferTo(0, sourceChannel.size(), destinationChannel);

		sourceChannel.close();
		destinationChannel.close();
	}

	/**
	 * 파일에 데이타를 쓴다.
	 * 
	 * @param f
	 *            데이타를 쓸 파일
	 * @param buf
	 *            데이타
	 * @param overwrite
	 *            동일한 파일명이 있을 경우 덮어쓸지 아니면 numbering을 증가시켜서 새로 만들지 여부
	 * @throws IOException
	 */
	public static File writeToFile(File file, byte[] buf, boolean overwrite)
			throws IOException {
		BufferedOutputStream out = null;
		String filename = file.getCanonicalPath();
		try {
			file = getUiqueFileName(filename, overwrite);
			out = new BufferedOutputStream(new FileOutputStream(file));

			out.write(buf, 0, buf.length);
			out.close();
		} finally {
			if (out != null)
				out.close();
		}
		
		return file;
	}

	/**
	 * 
	 * @param filename
	 * @param buf
	 * @param overwrite
	 * @return
	 * @throws IOException
	 */
	public static File writeToFile(String filename, byte[] buf,
			boolean overwrite) throws IOException {
		return writeToFile(new File(filename), buf, overwrite);
	}

	/**
	 * 
	 * @param filename
	 * @param msg
	 * @param overwrite
	 * @return
	 * @throws IOException
	 */
	public static File writeToFile(String filename, String msg,
			boolean overwrite) throws IOException {
		return writeToFile(new File(filename), msg.getBytes(), overwrite);
	}

	/**
	 * 파일에 데이타를 쓴다.
	 * 
	 * @param f
	 *            데이타를 쓸 파일
	 * @param buf
	 *            데이타
	 * @throws IOException
	 */
	public static File writeToFile(File file, byte[] buf) throws IOException {
		return writeToFile(file, buf, true);
	}

	/**
	 * 
	 * @param filename
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static File writeToFile(String filename, String str)
			throws IOException {
		return writeToFile(new File(filename), str.getBytes(), true);
	}

	/**
	 * 
	 * @param filename
	 * @param buf
	 * @return
	 * @throws IOException
	 */
	public static File writeToFile(String filename, byte[] buf)
			throws IOException {
		return writeToFile(new File(filename), buf, true);
	}

	/**
	 * 파일의 데이타를 읽어온다. 접수 증서 인증서 파일 값을 읽어오기 위해 사용된다.
	 * 
	 * @param filepath
	 *            파일 명
	 * @return 파일 데이타
	 * @throws IOException
	 */
	public static byte[] getFileByte(File file) throws IOException {
		// default window
		byte[] bytes = null;
		int offset = 0;
		int numRead = 0;
		BufferedInputStream bin = null;

		try {
			bytes = new byte[(int) file.length()];
			bin = new BufferedInputStream(new FileInputStream(file));

			while (offset < bytes.length
					&& (numRead = bin
							.read(bytes, offset, bytes.length - offset)) >= 0) {
				offset += numRead;
			}

			bin.close();
		} finally {
			if (bin != null)
				try {
					bin.close();
				} catch (IOException e) { /* ignore */
				}
		}

		return bytes;
	}

	/**
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static byte[] getFileByte(String filename) throws IOException {
		return getFileByte(new File(filename));
	}

	/**
	 * 
	 * @param fileName
	 * @return
	 * @throws IOException
	 */
	public static String getFileString(File fileName) throws IOException {
		byte[] buf = getFileByte(fileName);
		return new String(buf, 0, buf.length);
	}

	/**
	 * 
	 * @param filename
	 * @return
	 * @throws IOException
	 */
	public static String getFileString(String filename) throws IOException {
		return getFileString(new File(filename));
	}

	/**
	 * 
	 * @param dir
	 */
	public static void checkDirectory(String dir) {
		File file = new File(dir);
		if (file.exists() && file.isDirectory())
			return;

		file.mkdir();
	}

	/**
	 * 
	 * @param path
	 * @param suffix
	 * @return
	 */
	public static String replaceSuffix(String path, String suffix) {
		int idx = path.lastIndexOf('.');
		
		if (idx <= 0) {
			return path + suffix;
		}

		path = path.substring(0, idx);
		return path + "." + suffix;

	}

	/**
	 * filename__number.ext 형식으로 파일을 재생성한다. 이때 두개의 _ 이 실제 파일 명과 number를 구분하는 기준이
	 * 된다.
	 * 
	 * @param filename
	 * @return
	 */
	public static String incrementFileNumber(String filename) {
		String newFilename = "";
		int dotIdx = filename.lastIndexOf('.');
		String ext = filename.substring(dotIdx);

		int number = 1;
		int numIdx = filename.lastIndexOf("_[");
		
		if (numIdx > 0) {
			number = Integer.parseInt(filename
					.substring(numIdx + 2, dotIdx - 1));
			number++;
			newFilename = filename.substring(0, numIdx) + "_[" + number + "]";
		} else {
			newFilename = filename.substring(0, dotIdx) + "_[" + number + "]";
		}
		
		return newFilename + ext;
	}

	/**
	 * 
	 * @param filename
	 * @param overwrite
	 * @return
	 */
	public static File getUiqueFileName(String filename, boolean overwrite) {
		File target = new File(filename);
		
		if (!overwrite) {
			while (target.exists()) {
				filename = incrementFileNumber(filename);
				target = new File(filename);
			}
		}
		
		return target;
	}

	/**
	 * ZIP 파일 압축을 해제한다.
	 * 
	 * @param source
	 *            압축을 풀 ZIP 파일(byte 배열)
	 * @param destination
	 * @return
	 * @throws IOException
	 */
	public static Map<String, byte[]> unzip(byte[] source, String destination)
			throws IOException {
		Map<String, byte[]> files = new HashMap<String, byte[]>();

		// Open Zip file for reading
		ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(
				source));
		ZipEntry entry = null;

		while ((entry = zipIn.getNextEntry()) != null) {
			logger.info("Extracting: " + entry + " ,  size : "
					+ entry.getSize());

			if (entry.getSize() < 1)
				continue;

			// extract file if not a directory
			if (!entry.isDirectory()) {
				byte data[] = new byte[(int) entry.getSize()];

				int offset = 0;
				int numRead = 0;

				while (offset < data.length
						&& (numRead = zipIn.read(data, offset, data.length
								- offset)) >= 0) {
					offset += numRead;
				}

				files.put(destination + entry.getName(), data);
				zipIn.closeEntry();
			}
		}

		zipIn.close();
		return files;
	}

	/**
	 * 
	 * @param folder
	 * @throws Exception
	 */
	public static void zipping(File[] filesToZip, String targetName)
			throws IOException {

		byte[] buffer = new byte[1024 * 10];

		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				targetName));
		out.setLevel(Deflater.DEFAULT_COMPRESSION);

		for (int i = 0; i < filesToZip.length; i++) {
			// Associate a file input stream for the current file
			FileInputStream in = new FileInputStream(filesToZip[i]);

			// Add ZIP entry to output stream.
			out.putNextEntry(new ZipEntry(filesToZip[i].getName()));

			int len;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}

			// Close the current entry
			out.closeEntry();
			// Close the current file input stream
			in.close();
		}
		// Close the ZipOutPutStream
		out.close();
	}
}
