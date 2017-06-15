package de.csw;

import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.fail;

public class ImportTest {

	private static void assertFileExists(String relPath) {
		URL url = Thread.currentThread().getContextClassLoader().getResource(".");
		if (url==null) {
			fail("FATAL: Unable to locate current directory");
		}
		try {
			File f = new File(url.getPath() + relPath);
			if (!f.exists()) {
				fail("Unable to find file " + f.getPath() + "in target folder");
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail("Unable to find file " + relPath + "in target folder");
		}
	}

	@Test
	public void testCatalog() {
		System.out.println("Trying to find the files");
		assertFileExists( "../owl_tgt/pizza.owl" );
		assertFileExists( "../owl_tgt/catalog.xml" );
		assertFileExists( "../owl_tgt/imports/protege.owl" );
	}
}
