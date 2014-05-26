/**
 * 
 */
package de.csw.cl.importer.test;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import util.XMLUtil;
import de.csw.cl.importer.MainForMaven;
import de.csw.cl.importer.algorithm.CLImportationAlgorithm;
import de.csw.cl.importer.model.ConflictingTitlingException;

/**
 * @author ralph
 * 
 */
public class ClImportTestRunner {

	private static final Logger LOG = Logger
			.getLogger(ClImportTestRunner.class);

	/**
	 * R
	 * @param args
	 */
	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.err.println("Please pass the path to the base directory (the directory containing all 'caseX' directories as an argument.)");
			System.exit(-1);
		}
		
		File baseDir = new File(args[0]);
		
		String testCases = "ABCDEFGHIJKL";
		
		for (int i = 0; i < testCases.length(); i++) {
			File caseDir = new File(baseDir, "case" + testCases.charAt(i));
			File inputDir = new File(caseDir, "input");
			File[] xclFiles = inputDir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".xcl");
				}
			});
			for (File xclFile : xclFiles) {
				System.out.println("Running algorithm on file " + xclFile.getAbsolutePath());
				CLImportationAlgorithm algo = new CLImportationAlgorithm(xclFile);
				try {
					algo.run();
				} catch (ConflictingTitlingException e) {
					System.err.println("Conflicting titlings (same name, different content) have been detected: " + e.getName() + ". Aborting.");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
