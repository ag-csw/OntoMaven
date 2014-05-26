/**
 * 
 */
package de.csw.cl.importer.algorithm;

/**
 * @author ralph
 *
 */
public class FolderCreationException extends Exception {

	private static final long serialVersionUID = -563493365550549459L;

	public FolderCreationException() {
		super();
	}

	public FolderCreationException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FolderCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public FolderCreationException(String message) {
		super(message);
	}

	public FolderCreationException(Throwable cause) {
		super(cause);
	}
	
}
