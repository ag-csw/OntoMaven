package de.csw;

import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.fail;

public class ImportTest {

	private static void assertFileExists(String fileName) {
		URL url = ImportTest.class.getResource(fileName);
		if (url==null) {
			fail("Unable to find file " + fileName + "in target folder");
		}
		try {
			File f = new File(url.toURI());
			if (!f.exists()) {
				fail("Unable to find file " + fileName + "in target folder");
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
			fail("Unable to find file " + fileName + "in target folder");
		}
	}

	@Test
	public void testCatalog() {
		assertFileExists( "/owl/pizza.owl" );
		assertFileExists( "/owl/catalog.xml" );
		assertFileExists( "/owl/imports/protege.owl" );
	}
}
