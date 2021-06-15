package org.scaffoldeditor.scaffold.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.lingala.zip4j.io.inputstream.ZipInputStream;
import net.lingala.zip4j.model.LocalFileHeader;

/**
 * This utility compresses a list of files to standard ZIP format file.
 * It is able to compress all sub files and sub directories, recursively.
 * @author www.codejava.net
 *
 */
public class ZipUtils {
    /**
     * A constants for buffer size used to read/write data
     */
    private static final int BUFFER_SIZE = 4096;
    /**
     * Compresses a list of files to a destination zip file
     * @param listFiles A collection of files and directories
     * @param destZipFile The path of the destination zip file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void zip(List<File> listFiles, String destZipFile) throws FileNotFoundException,
            IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));
        for (File file : listFiles) {
            if (file.isDirectory()) {
                zipDirectory(file, file.getName(), zos);
            } else {
                zipFile(file, zos);
            }
        }
        zos.flush();
        zos.close();
    }
    /**
     * Compresses files represented in an array of paths
     * @param files a String array containing file paths
     * @param destZipFile The path of the destination zip file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void zip(String[] files, String destZipFile) throws FileNotFoundException, IOException {
        List<File> listFiles = new ArrayList<File>();
        for (int i = 0; i < files.length; i++) {
            listFiles.add(new File(files[i]));
        }
        zip(listFiles, destZipFile);
    }
    /**
     * Adds a directory to the current zip output stream
     * @param folder the directory to be added
     * @param parentFolder the path of parent directory
     * @param zos the current zip output stream
     * @throws FileNotFoundException
     * @throws IOException
     */
	private static void zipDirectory(File folder, String parentFolder,
            ZipOutputStream zos) throws FileNotFoundException, IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
                continue;
            }
            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(file));
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read = 0;
            while ((read = bis.read(bytesIn)) != -1) {
                zos.write(bytesIn, 0, read);
            }
            bis.close();
            zos.closeEntry();
        }
    }
    /**
     * Adds a file to the current zip output stream
     * @param file the file to be added
     * @param zos the current zip output stream
     * @throws FileNotFoundException
     * @throws IOException
     */
	private static void zipFile(File file, ZipOutputStream zos)
            throws FileNotFoundException, IOException {
        zos.putNextEntry(new ZipEntry(file.getName()));
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
                file));
        byte[] bytesIn = new byte[BUFFER_SIZE];
        int read = 0;
        while ((read = bis.read(bytesIn)) != -1) {
            zos.write(bytesIn, 0, read);
        }
        bis.close();
        zos.closeEntry();
    }
	
	public static void extractZip(ZipInputStream is, Path targetDir) throws IOException {
		LocalFileHeader localFileHeader;
		int readLen;
		byte[] readBuffer = new byte[BUFFER_SIZE];
		
		while ((localFileHeader = is.getNextEntry()) != null) {
			File extractedFile = targetDir.resolve(localFileHeader.getFileName()).toFile();
			File parent = extractedFile.getParentFile();
			if (parent.isFile()) parent.delete();
			parent.mkdirs();
			
			OutputStream os = new FileOutputStream(extractedFile);
			while ((readLen = is.read(readBuffer)) != -1) {
				os.write(readBuffer, 0, readLen);
			}
			os.close();
		}
	}
}