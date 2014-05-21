package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Util class for some file operations.
 */
public class FileUtil {
	
	/**
	 * Returns the content of a given local file as String.
	 */
	public static String loadLocalFileToString(File file) {
		try {
			return new String(Files.readAllBytes(Paths.get(file
					.getAbsolutePath())));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Writes a given String into a given File.
	 */
	public static void writeStringToFile(File file, String content) {
		try {
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(content);
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks for given path if the target file is existing.
	 */
	public static boolean existsHTTPFile(String path) {
		try {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK)
				return true;
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Returns the content of the target file of a given path as String.
	 */
	public static String loadRemoteFileAsString(String path) {
		 String content = "";
		try {
			URL url = new URL(path);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					url.openStream()));
			String line;
			while ((line = in.readLine()) != null)
				content += line;
			in.close();
		} catch (IOException e) {
			System.err.println("Cannot load file " + path + System.lineSeparator() +  e.getMessage());
			return null;
		}
		return content;
	}

}
	