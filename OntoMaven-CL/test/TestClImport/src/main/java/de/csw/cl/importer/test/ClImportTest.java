/**
 * 
 */
package de.csw.cl.importer.test;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import junit.framework.TestCase;

import org.jdom2.Document;
import org.jdom2.output.XMLOutputter;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import util.XMLUtil;
import de.csw.cl.importer.algorithm.CLImportationAlgorithm;
import de.csw.cl.importer.model.ConflictingTitlingException;

/**
 * Unit test for the CL import algorithm.
 * @author ralph
 */
@RunWith(value = Parameterized.class)
public class ClImportTest extends TestCase {
	
	private static File testBaseDir;
	private static File expectedResultBaseDir;
	
	
	@BeforeClass
	public static void setup() {
		String baseDirPath = System.getProperty("baseDir");
		String expectedResultsDirPath = System.getProperty("expectedResultBaseDir");

		if (baseDirPath == null) {
			System.err.println("Please pass the path to the base directory (the directory containing all 'caseX' directories as a system property (VM argument in eclipse) -DbaseDir=<path to base directory>). For comparison with the expected results, please specify the 'expectedResultBaseDir' parameter (-expectedResultBaseDir=<path to examples directory>)");
			System.exit(-1);
		}
		testBaseDir = new File(baseDirPath);
		
		if (expectedResultsDirPath == null) {
			expectedResultBaseDir = testBaseDir;
		} else {
			expectedResultBaseDir = new File(expectedResultsDirPath);
		}
	}

	@Parameters
	public static Iterable<Object[]> data() {
		
		// { { "case directory name"}, { <expected exception type> or null } }, ...
		
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
			   { "caseM", null },
               { "caseN", null },
               { "caseP", null },
               { "caseQ", null },
               { "caseR", null },
               { "caseS", null }
			   };
		return Arrays.asList(data);
	}
	
	private static String caseDirName;
	private Class<Throwable> expectedThrowable;
	
	public ClImportTest(String caseDirName, Class<Throwable> expectedThrowable) {
		if (System.getProperty("baseDir") == null) {
			System.err.println("Please pass the path to the base directory (the directory containing all 'caseX' directories as an argument.)");
			System.exit(-1);
		}
		ClImportTest.caseDirName = caseDirName;
		this.expectedThrowable = expectedThrowable;
	}
	
	
	/**
	 * 
	 * @param args
	 */
	@Test
	public void testAll() {
		File testCaseDir = new File(testBaseDir, caseDirName); // the directory for this test case ("<baseDir>/caseX")
		File testInputDir = new File(testCaseDir, "input"); // the directory containing the input corpus ("<baseDir>/caseX/input")
		File testResultDir = new File(testCaseDir, "test-result"); // the directory containing the expected output ("<expectedResultBaseDir>/caseX/test-result")
		
		
		File expectedResultDir = new File(new File(expectedResultBaseDir, (caseDirName)), "result");

		// hard-coded exception
		if(caseDirName.equals("caseF")) {
			expectedResultDir = new File(new File(expectedResultBaseDir, (caseDirName)), "result2");
		}

		// delete stale files in test-result dir from previous runs
		
		try {
			Files.walkFileTree(testResultDir.toPath(), new FileVisitor<Path>() {

				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult visitFile(Path file,
						BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult visitFileFailed(Path file,
						IOException exc) throws IOException {
					return FileVisitResult.CONTINUE;
				}

				public FileVisitResult postVisitDirectory(Path dir,
						IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		// run algorithm on folder and fail if an inexpected exception is caught
			
		CLImportationAlgorithm algo = new CLImportationAlgorithm(testInputDir);
		
		try {
			algo.run(testResultDir);
			if (expectedThrowable != null ) {
				System.err.println("Exception of type " + expectedThrowable.getCanonicalName() + " expected, but none has been thrown.");
				fail("Exception of type " + expectedThrowable.getCanonicalName() + " expected, but none has been thrown.");
			}
		} catch (ConflictingTitlingException e) {
			if (expectedThrowable != null && expectedThrowable.isAssignableFrom(ConflictingTitlingException.class)) {
				System.out.println("Conflicting titlings (same name, different content) have been detected as expected: "
								+ e.getName() + ".");
			} else {
				System.err.println("Unexpected conflicting titlings with name " + e.getName());
				fail("Unexpected conflicting titlings with name " + e.getName());
			}
		} catch (Throwable t) {
			if (expectedThrowable == null || !expectedThrowable.isAssignableFrom(t.getClass())) {
				System.err.println("Unexpected exception : " + t.getMessage());
				t.printStackTrace();
				fail("Unexpected exception : " + t.getMessage());
			}
		}
		
		// Check for missing/unexpected files
		
		if (!expectedResultDir.exists() && testResultDir.exists()) {
			fail("No results directory should have been created.");
		}
		
		String[] expectedFileNames = expectedResultDir.list(systemFileNameFilter);
		String[] testResultFileNames = testResultDir.list(systemFileNameFilter);
		
		File expectedIncludesDir = new File(expectedResultDir, "includes");
		File testResultIncludesDir = new File(testResultDir, "includes");

//		String[] expectedIncludeFileNames = expectedIncludesDir.list();
//		String[] testResultIncludeFileNames = testResultIncludesDir.list();
		
		if (!Arrays.equals(testResultFileNames, expectedFileNames)
//				|| !Arrays.equals(testResultIncludeFileNames, expectedIncludeFileNames)
				) {
			// let's take a closer look

			ArrayList<String> missingFiles = new ArrayList<String>(); 
			ArrayList<String> unexpecedFiles = new ArrayList<String>();

			List<String> expectedFileNameList = Arrays.asList(expectedFileNames);
			List<String> testResultFileNameList = Arrays.asList(testResultFileNames);
			
//			List<String> expectedIncludeFileNameList = Arrays.asList(expectedIncludeFileNames);
//			List<String> testResultIncludeFileNameList = Arrays.asList(testResultIncludeFileNames);
			
			listDiff(testResultFileNameList, expectedFileNameList, missingFiles, unexpecedFiles, "");
//			listDiff(testResultIncludeFileNameList, expectedIncludeFileNameList, missingFiles, unexpecedFiles, "includes/");
			
			StringBuilder buf = new StringBuilder("Files are missing and/or unexpected files were created\n");
			
			if (!unexpecedFiles.isEmpty()) {
				buf.append("Unexpected:\n");
				for (String fileName : unexpecedFiles) {
					buf.append(fileName);
					buf.append('\n');
				}
			}
			
			if (!missingFiles.isEmpty()) {
				buf.append("Missing:\n");
				for (String fileName : missingFiles) {
					buf.append(fileName);
					buf.append('\n');
				}
			}
			
			fail(buf.toString());
		}
		
		// compare files in the result directory

        File[] expectedResultFiles = expectedResultDir.listFiles(new FileFilter() {
            
            public boolean accept(File file) {
                return file.isFile() &&
                        (file.getName().toLowerCase().endsWith(".xml") ||
                                file.getName().toLowerCase().endsWith(".xcl"));
            }
        });

        File[] testResultFiles = testResultDir.listFiles(new FileFilter() {
            
            public boolean accept(File file) {
                return file.isFile() &&
                        (file.getName().toLowerCase().endsWith(".xml") ||
                                file.getName().toLowerCase().endsWith(".xcl"));
            }
        });

		if (!(testResultFiles == null)) {
    		for (File testResultFile : testResultFiles) {
    			if (testResultFile.isFile()) {
    				
    				File expectedFile = new File(expectedResultDir, testResultFile.getName());
    				Document expectedDocument = XMLUtil.readLocalDoc(expectedFile);
    				Document testResultDocument = XMLUtil.readLocalDoc(testResultFile);
    				if (!XMLUtil.getCanonicalXML(testResultDocument).equals(XMLUtil.getCanonicalXML(expectedDocument))) {
    					XMLOutputter xmlOutputter = new XMLOutputter();
    					
                        StringWriter out = new StringWriter();
                        StringWriter outExpected = new StringWriter();
    					try {
                            xmlOutputter.output(testResultDocument, out);
                            xmlOutputter.output(expectedDocument, outExpected);
    					} catch (IOException e) {
    						fail();
    					}
    					
    					fail("Content not as expected in " + testResultFile.getName() + "\n\n" + out.toString()
    					        + "\n\n" + "SHOULD BE" + "\n\n" + outExpected.toString() + "\n\n");
    				}
    			}
    			else {
    			    fail("Not a File");
    			}
    		}
    		
		}
		else {
		    if(!(expectedResultFiles == null)){
		        fail("No test results files when files are expected.");
		    }
		        
		}
		
		// ... and now in the includes directory
		// Note: We cannot perform a comparison of file names since included files are named based on the hash code over their content, while in the test cases, they are named 1.xml, 2.xml, etc.
		
		File[] testResultIncludeFiles = testResultIncludesDir.listFiles(systemFileNameFilter);
		File[] expectedIncludeFiles = expectedIncludesDir.listFiles(systemFileNameFilter);
		
		HashSet<String> expectedIncludeFileContents = new HashSet<String>();
        ArrayList<String> missingFiles = new ArrayList<String>();

        if(!(expectedIncludeFiles == null)) {
    		for (File includeFile : expectedIncludeFiles) {
    			String expectedCanonicalXML = XMLUtil.getCanonicalXML(XMLUtil.readLocalDoc(includeFile));
    			expectedIncludeFileContents.add(expectedCanonicalXML);
    			
    			System.out.println("\n -- " + includeFile.getAbsolutePath() + " -->\n\n" + expectedCanonicalXML + "\n------------\n\n");
                Boolean missing = true;
    	        if(!(testResultIncludeFiles == null)) {
    	            for (File testIncludeFile : testResultIncludeFiles) {
    	                String canonicalXML = XMLUtil.getCanonicalXML(XMLUtil.readLocalDoc(testIncludeFile));
                        if (canonicalXML.equals(expectedCanonicalXML)) {
                            missing = false;
                        }    	                
    	            }
    	        }
                if (missing)
                    missingFiles.add(includeFile.getName());
                }    		    
    		}
		
		ArrayList<String> unmatchedFiles = new ArrayList<String>();
		
        if(!(testResultIncludeFiles == null)) {
    		for (File includeFile : testResultIncludeFiles) {
    			String canonicalXML = XMLUtil.getCanonicalXML(XMLUtil.readLocalDoc(includeFile));
    			if (!expectedIncludeFileContents.contains(canonicalXML)) {
    				unmatchedFiles.add(includeFile.getName());
    			}
    			
    			System.out.println("\n -- " + includeFile.getAbsolutePath() + " -->\n\n" + canonicalXML + "\n------------\n\n");
    		}
        }

        if (!missingFiles.isEmpty()) {
			StringBuilder buf = new StringBuilder("Missing include files:\n");
			
			for (String fileName : missingFiles) {
				buf.append("includes/" + fileName);
				buf.append('\n');
			}
			
			fail(buf.toString());
        }
        if (!unmatchedFiles.isEmpty()) {
            StringBuilder buf = new StringBuilder("Unmatched include files:\n");
            
            for (String fileName : unmatchedFiles) {
                buf.append("includes/" + fileName);
                buf.append('\n');
            }
            
            fail(buf.toString());
        }
	}
	
	
	/**
	 * Compares a given list with a list of expected objects and adds any missing or unexpected entries to the missingEntries and unexpectedEntries lists.
	 * @param testList the list 
	 * @param expectedList
	 * @param missingEntries
	 * @param unexpectedEntries
	 */
	private void listDiff(List<String> testList, List<String> expectedList, List<String> missingEntries, List<String> unexpectedEntries, String prefix) {

		for (String entry : expectedList) {
			if (!testList.contains(entry)) {
				missingEntries.add(prefix + entry);
			}
		}

		for (String entry : testList) {
			if (!expectedList.contains(entry)) {
				unexpectedEntries.add(prefix + entry);
			}
		}
	}
	
	
    public static void fail(String message) {
    	TestCase.fail(caseDirName + ": " + message);
    }
    
    private FilenameFilter systemFileNameFilter = new FilenameFilter() {
		
		public boolean accept(File dir, String name) {
			return !name.equals(".DS_Store");
		}
	};
}
