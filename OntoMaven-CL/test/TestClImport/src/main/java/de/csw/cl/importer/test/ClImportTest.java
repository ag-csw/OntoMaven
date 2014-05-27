/**
 * 
 */
package de.csw.cl.importer.test;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

import junit.framework.TestCase;

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
 * 
 * @author ralph
 */
@RunWith(value = Parameterized.class)
public class ClImportTest extends TestCase {

    private static File baseDir;

    @BeforeClass
    public static void setup() {
        String baseDirPath = System.getProperty("baseDir");
        if (baseDirPath == null) {
            System.err
                    .println("Please pass the path to the base directory (the directory containing all 'caseX' directories as a system property (VM argument in eclipse) -DbaseDir=<path to base directory>.)");
            System.exit(-1);
        }
        baseDir = new File(baseDirPath);
    }

    @Parameters
    public static Iterable<Object[]> data() {
        Object[][] data = new Object[][] { { "caseA", null },
                { "caseB", null }, { "caseC", null }, { "caseD", null },
                { "caseE", null }, { "caseF", null }, { "caseG", null },
                { "caseH", ConflictingTitlingException.class },
                { "caseI", null }, { "caseJ", null }, { "caseK", null },
                { "caseL", null }, { "caseM", null }, { "caseN", null } };
        return Arrays.asList(data);
    }

    private final String caseDirName;
    private final Class<Throwable> expectedThrowable;

    public ClImportTest(String caseDirName, Class<Throwable> expectedThrowable) {
        if (System.getProperty("baseDir") == null) {
            System.err
                    .println("Please pass the path to the base directory (the directory containing all 'caseX' directories as an argument.)");
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

        boolean fail = false;

        // run algorithm on folder and fail if an inexpected exception is caught
        System.out.println("Running algorithm on file "
                + caseDir.getAbsolutePath());

        CLImportationAlgorithm algo = new CLImportationAlgorithm(caseDir);

        try {
            algo.run();
            if (expectedThrowable != null) {
                System.err.println("Exception of type "
                        + expectedThrowable.getCanonicalName()
                        + " expected, but none has been thrown.");
                fail = true;
            }
        } catch (ConflictingTitlingException e) {
            if (expectedThrowable != null
                    && expectedThrowable
                            .isAssignableFrom(ConflictingTitlingException.class)) {
                System.out
                        .println("Conflicting titlings (same name, different content) have been detected as expected: "
                                + e.getName() + ".");
            } else {
                System.err.println("Unexpected conflicting titlings with name "
                        + e.getName());
                fail = true;
            }
        } catch (Throwable t) {
            if (expectedThrowable == null
                    || !expectedThrowable.isAssignableFrom(t.getClass())) {
                System.err.println("Unexpected exceeption : "
                        + t.getLocalizedMessage());
                t.printStackTrace();
                fail = true;
            }
        }

        if (fail)
            fail();

        // TODO compare files

    }

}
