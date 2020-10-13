import de.csw.ontomaven.ImportOntologies;
import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.protege.xmlcatalog.CatalogUtilities;
import org.protege.xmlcatalog.XMLCatalog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ImportTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testImportChain() {

		ImportOntologies mojo = new ImportOntologies();
		try {
			File temp = new File("target/testImport");
			temp.mkdir();
			assertEquals( 0, temp.listFiles().length );

			URL url = ImportTest.class.getResource( "o1.rdf" );
			URL cat = ImportTest.class.getResource( "catalog-v001.xml" );

			mojo.setOwlFileURL( url.toString() );
			mojo.setImportDirectory( "." );
			mojo.setOwlFileName( "o1-local.rdf" );
			mojo.setMappingCatalogURL( cat.toString() );
			mojo.setOwlDirectory( "testImport" );
			mojo.setCatalogFileName( "catalog.xml" );

			mojo.execute();

			Set<String> names = Arrays.stream( temp.listFiles() ).map( File::getName ).collect( Collectors.toSet() );
			System.out.print( names );

			assertEquals( 4, temp.listFiles().length );
			assertTrue( names.contains( "o1-local.rdf" ) );
			assertTrue( names.contains( "o2.rdf" ) );
			assertTrue( names.contains( "o3.rdf" ) );
			assertTrue( names.contains( "catalog.xml" ) );

			File catalog = temp.listFiles( ( dir, name ) -> name.equals( "catalog.xml" ) )[0];

			XMLCatalog xmlCatalog = CatalogUtilities.parseDocument( catalog.toURI().toURL() );
			assertEquals( 2, xmlCatalog.getEntries().size() );

			URI tgt = CatalogUtilities.getRedirect( URI.create( "http://test.org/o2" ), xmlCatalog );
			File o2 = temp.listFiles( ( dir, name ) -> name.equals( "o2.rdf" ) )[0];

			assertEquals( o2.toURI().toString(), tgt.toString() );

			FileUtils.deleteDirectory(temp);

		} catch ( Exception e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


	@Test
	public void testImportPizza() {

		ImportOntologies mojo = new ImportOntologies();
		try {
			File temp = new File("target/testImport");
			temp.mkdir();
			assertEquals( 0, temp.listFiles().length );

			URL url = ImportTest.class.getResource( "pizza.owl" );

			mojo.setOwlFileURL( url.toString() );
			mojo.setImportDirectory( "." );
			mojo.setOwlFileName( "pizza.owl" );
			mojo.setCatalogFileName( "catalog.xml" );
			mojo.setOwlDirectory( "testImport" );

			mojo.execute();

			Set<String> names = Arrays.stream( temp.listFiles() ).map( File::getName ).collect( Collectors.toSet() );
			assertEquals( 3, names.size() );


			FileUtils.deleteDirectory(temp);

		} catch ( Exception e ) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}


}
