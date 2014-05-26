/**
 * 
 */
package de.csw.cl.importer.test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.csw.cl.importer.algorithm.CLImportationAlgorithm;
import de.csw.cl.importer.model.ConflictingTitlingException;

/**
 * Unit test for the CL import algorithm.
 * @author ralph
 */
@RunWith(value = Parameterized.class)
public class ClImportTest {
	
	private static File baseDir;
	
	
	@BeforeClass
	public static void setup() {
		String baseDirPath = System.getProperty("baseDir");
		if (baseDirPath == null) {
			System.err.println("Please pass the path to the base directory (the directory containing all 'caseX' directories as a system property (VM argument in eclipse) -DbaseDir=<path to base directory>.)");
			System.exit(-1);
		}
		baseDir = new File(baseDirPath);
	}

	@Parameters
	public static Iterable<Object[]> data() {
		Object[][] data = new Object[][] {
			   { "caseA", null },
			   { "caseB", null },
			   { "caseC", null },
			   { "caseD", null },
			   { "caseE", null },
			   { "caseF", null },
			   { "caseG", null },
			   { "caseH", ConflictingTitlingException.class },
			   { "caseI", null },
			   { "caseJ", null },
			   { "caseK", null },
			   { "caseL", null },
			   { "caseN", null }
			   };
		return Arrays.asList(data);
	}
	
	private String caseDirName;
	private Class<Throwable> expectedThrowable;
	
	public ClImportTest(String caseDirName, Class<Throwable> expectedThrowable) {
		if (System.getProperty("baseDir") == null) {
			System.err.println("Please pass the path to the base directory (the directory containing all 'caseX' directories as an argument.)");
			System.exit(-1);
		}
		this.caseDirName = caseDirName;
		this.expectedThrowable = expectedThrowable;
	}
	
	
	/**
	 * 
	 * @param args
	 */
	@Test
	public void testAll() {
		File caseDir = new File(baseDir, caseDirName);
		File inputDir = new File(caseDir, "input");
		
		File[] xclFiles = inputDir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".xcl");
			}
		});
		
		// run algorithm on folder and fail if an inexpected exception is caught
		for (File xclFile : xclFiles) {
			System.out.println("Running algorithm on file "
					+ xclFile.getAbsolutePath());
			
			CLImportationAlgorithm algo = new CLImportationAlgorithm(xclFile);
			
			try {
				algo.run();
			} catch (ConflictingTitlingException e) {
				if (expectedThrowable != null && expectedThrowable.isAssignableFrom(ConflictingTitlingException.class)) {
					System.out.println("Conflicting titlings (same name, different content) have been detected as expected: "
									+ e.getName() + ". Aborting.");
				} else {
					assert(false);
				}
			} catch (Throwable t) {
				if (expectedThrowable == null || !expectedThrowable.isAssignableFrom(t.getClass())) {
					assert(false);
				}
			}
		}
		
		// TODO compare files
		
		
	}
	
	

}
