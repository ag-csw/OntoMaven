/**
 * 
 */
package de.csw.cl.importer;

import java.io.File;

/**
 * @author ralph
 * 
 */
public class ClImporter {

	private File baseFile;
	private File inputDirectory;
	private File resultsDirectory;

	/**
	 * 
	 * @param baseFile
	 * @param resultsDirectory
	 */
	public ClImporter(File baseFile, File resultsDirectory) {
		this.baseFile = baseFile;
		this.inputDirectory = baseFile.getParentFile();
		this.resultsDirectory = resultsDirectory;
	}

	public void executeImportations() {
		
	}

	/**
	 * Loads the corpus into memory. For now, it is assumed that all files in
	 * the input directory constitute the corpus.
	 * 
	 * @todo In a future version, load the corpus by recursively dereferencing
	 *       all import URIs and loading the resources from their physical
	 *       location, tracing possible cyclic importations.
	 */
	private void loadCorpus() {
		
	}

}
