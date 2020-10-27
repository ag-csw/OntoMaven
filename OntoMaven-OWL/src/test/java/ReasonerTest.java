import de.csw.ontomaven.ImportOntologies;
import de.csw.ontomaven.TestOntology;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;
import org.semanticweb.owlapi.reasoner.InferenceType;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ReasonerTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testReasoner() {

		TestOntology mojo = new TestOntology();
		try {
			File temp = folder.newFolder();
			assertEquals( 0, temp.listFiles().length );

			URL url = ReasonerTest.class.getResource( "pizza.owl" );
			File f = new File( url.getPath() );
			mojo.setOwlFileName( f.getName() );
			mojo.setOwlDirectory( "test-classes" );

			mojo.execute();

		} catch ( Exception e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testExplanation() {

		TestOntology mojo = new TestOntology();
		try {
			File temp = folder.newFolder();
			assertEquals( 0, temp.listFiles().length );

			URL url = ReasonerTest.class.getResource( "broken.rdf" );
			File f = new File( url.getPath() );
			mojo.setOwlFileName( f.getName() );
			mojo.setOwlDirectory( "test-classes" );

			StringBufferLog log = new StringBufferLog();
			mojo.setLog( log );

			mojo.execute();

			System.out.println( log.getLogData() );

			assertTrue( log.getLogData().contains( "DisjointClasses(<http://test.org/inconsistent#A> <http://test.org/inconsistent#B>)" ) );

		} catch ( Exception e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


}
