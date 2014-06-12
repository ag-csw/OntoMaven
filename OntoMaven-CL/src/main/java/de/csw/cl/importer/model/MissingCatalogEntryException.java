package de.csw.cl.importer.model;

public class MissingCatalogEntryException extends Exception {

    /**
     * This Exception is supposed to be thrown when there is an expectation
     * that there is an entry in the catalog that is not there. 
     */
    private static final long serialVersionUID = 5662721660241287499L;
    private String uri;

    /**
     * @param uri
     */
    public MissingCatalogEntryException(String uri) {
        this.uri = uri;
    }
    /**
     * Returns the missing key.
     * @return
     */
    public String getUri() {
        return uri;
    }
    
    
}