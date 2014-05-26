/**
 * 
 */
package de.csw.cl.importer.test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import de.csw.cl.importer.MainForMaven;

/**
 * @author ralph
 * 
 */
public class ClImportTestRunner {

	private static final Logger LOG = Logger
			.getLogger(ClImportTestRunner.class);

	private static final SimpleDateFormat timeStampFormat = new SimpleDateFormat(
			"yyyy-MM-dd-HH-mm-ss-SSSS");

	/**
	 * Copies all files from the examples directory to a new temporary folder
	 * and runs the imporation task on each of them.
	 * 
	 * @param args
	 *            The target folder of the test run where the temporary folder
	 *            with the results will be created. If omitted, the path
	 *            corresponding to the system property "java.io.tempdir" will be
	 *            used.
	 */
	public static void main(String[] args) {

		// Checking existence of target directory, trying to create if
		// non-existent, aborting on error.
		String targetParentFolderPath = args.length == 0 ? System
				.getProperty("java.io.tmpdir") : args[0];
		if (targetParentFolderPath == null) {
			LOG.error("Please specify a target directory where all the ");
		}
		File targetParentFolder = new File(targetParentFolderPath);

		if (!targetParentFolder.exists()) {
			LOG.info("Target folder " + targetParentFolderPath
					+ " does not exist. Trying to create it...");
			boolean success = targetParentFolder.mkdirs();
			if (!success) {
				LOG.error("Could not create target folder "
						+ targetParentFolderPath + ". Aborting.");
				System.exit(1);
			}
		}

		// trying to create a dedicated sub-directory for this test run
		File targetFolder = new File(targetParentFolder, "cl_import_test_run-"
				+ timeStampFormat.format(new Date()));
		if (!targetFolder.mkdir()) {
			LOG.error("Could not create test case target folder "
					+ targetFolder.getAbsolutePath() + ". Aborting.");
			System.exit(1);
		}

		try {

			// locating directory with test cases
			URL folder = ClImportTestRunner.class.getResource("/caseA");
			File sourceExamplesFolder = new File(folder.toURI())
					.getParentFile();

			// collect all test case directories
			File[] sourceCaseFolders = collectCaseFolders(sourceExamplesFolder);

			// copy all test case directories to the target folder
			for (File caseFolder : sourceCaseFolders) {
				try {
					copyDirectory(caseFolder,
							new File(targetFolder, caseFolder.getName()),
							"result");
				} catch (IOException e) {
					LOG.error("Error copying directory "
							+ caseFolder.getAbsolutePath() + " to "
							+ targetFolder.getAbsolutePath());
					throw e;
				}
			}

			File[] targetCaseFolders = collectCaseFolders(targetFolder);
			for (File caseFolder : targetCaseFolders) {

				// collecting all input directories in case folders
				File inputFolder = new File(caseFolder, "input");

				// collecting all input xcl files
				File[] clFiles = inputFolder.listFiles(new FileFilter() {
					public boolean accept(File file) {
						return file.isFile() && file.getName().endsWith(".xcl");
					}
				});

				// running the imporatation algorithm on each input xcl file
				for (File clFile : clFiles) {
					MainForMaven mavenTask = new MainForMaven();
					mavenTask.setClFile(clFile);
					File destinationDir = new File(caseFolder, "result");
					destinationDir.mkdirs();
					mavenTask.setDestinationDir(destinationDir);

					try {
						mavenTask.execute();
					} catch (Exception e) {
						LOG.error("Error running Maven import task on file "
								+ clFile.getAbsolutePath());
						LOG.error("Error message was: " + e.getMessage());
						throw e;
					}
				}
			}

		} catch (Exception e) {
			LOG.error("Aborting.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	/**
	 * Collects all directories named "case[A-Z]" in the given parent directory
	 * 
	 * @param parentDir
	 * @return an array of {@link File}s, each of them representing a directory
	 *         the name of which adheres to the pattern "case[A-Z]".
	 */
	private static File[] collectCaseFolders(File parentDir) {
		return parentDir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory()
						&& file.getName().matches("^case[A-Z]$");
			}
		});
	}

	// stolen from
	// http://www.java-tips.org/java-se-tips/java.io/how-to-copy-a-directory-from-one-location-to-another-loc.html
	private static void copyDirectory(File sourceLocation, File targetLocation,
			String exclude) throws IOException {

		if (sourceLocation.getName().equals(exclude))
			return;

		if (sourceLocation.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdir();
			}

			String[] children = sourceLocation.list();
			for (int i = 0; i < children.length; i++) {
				copyDirectory(new File(sourceLocation, children[i]), new File(
						targetLocation, children[i]), exclude);
			}
		} else {

			InputStream in = new FileInputStream(sourceLocation);
			OutputStream out = new FileOutputStream(targetLocation);

			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}

}
