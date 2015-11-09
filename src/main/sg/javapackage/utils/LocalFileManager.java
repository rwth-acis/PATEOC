package main.sg.javapackage.utils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * support function for read and write local files
 * @author Stephen
 *
 */
public class LocalFileManager {
	
	private static String baseDir = "";
    private static LocalFileManager manager;

    protected LocalFileManager() {

    }

    public static void setBasedir(String dir) {
    	baseDir = "D:\\Eclipse\\workspace";
    }

    protected static LocalFileManager getManager() {
		if (manager == null)
		    manager = new LocalFileManager();
	
		return manager;
    }

    public static byte[] getFile(String file) {
	getManager();
	/*
	 * if(file.contains(".."))//ignore
	 * return null;
	 */
	return getFile(new File(baseDir + file));
    }

    public static List<String> getDir(String dir) {
		try {
		    File directory = new File(baseDir + dir);
	
		    List<String> dirs = new ArrayList<String>();
		    List<String> files = new ArrayList<String>();
		    File[] dirContents = directory.listFiles();
		    for (final File fileEntry : dirContents) {
			if (fileEntry.isDirectory()) {
			    dirs.add(fileEntry.getName());
			} else {
			    files.add(fileEntry.getName());
			}
		    }
		    Collections.sort(dirs);
		    Collections.sort(files);
		    dirs.addAll(files);
		    return dirs;
		} catch (Exception e) {
		    return null;
		}

    }

    /**
     * Reads a given file
     * 
     * @param file
     *            file to read
     * @return content of file
     */
    public static byte[] getFile(File file) {

		byte[] result = new byte[] {};
	
		try {
		    result = Files.readAllBytes(file.toPath());
		} catch (IOException e) {

	}

	return result;
    }

    /**
     * Writes a string to a file
     * 
     * @param file
     *            file path
     * @param content
     *            what to write into the file
     * @throws IOException
     */
    public static void writeFile(String file, String content) throws IOException {
		PrintWriter writer = null;
		try {
		    writer = new PrintWriter(file, "UTF-8");
		    writer.write(content);
	
		}
		finally {
		    if (writer != null)
			writer.close();
		}
    }
    
    public static String getGraphAsString(String filename) {
    	return new String(LocalFileManager.getFile(filename));
    }


}
