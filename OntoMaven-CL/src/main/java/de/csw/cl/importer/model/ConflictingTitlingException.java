/**
 * 
 */
package de.csw.cl.importer.model;

import org.jdom2.Element;

/**
 * This Exception is supposed to be thrown whenever two conflicting Titlings are
 * encountered within the same CL text corpus. Two Titlings conflict if they
 * 
 * <ol>
 * <li>have the same name,</li>
 * <li>have (syntactically) different content, and</li>
 * <li>an import with that name is executed (the content of both would be asserted).</li>
 * </ol>
 * 
 * @author ralph
 */
public class ConflictingTitlingException extends Exception {

	private static final long serialVersionUID = 6497258748436567649L;
	
	private String name;

	private Element titlingElement1; 
	private Element titlingElement2;
	
	public ConflictingTitlingException(String name, Element titlingElement1,
			Element titlingElement2) {
		this.name = name;
		this.titlingElement1 = titlingElement1;
		this.titlingElement2 = titlingElement2;
	}

	/**
	 * Returns the name of the two conflicting Titlings.
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the first conflicting Titling element.
	 * @return
	 */
	public Element getTitlingElement1() {
		return titlingElement1;
	}

	/**
	 * Returns the seconf conflicting Titling element.
	 * @return
	 */
	public Element getTitlingElement2() {
		return titlingElement2;
	}
	
	

}
