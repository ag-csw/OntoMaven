package de.csw.cl.importer.model;

public class MissingIncludeEntryException extends Exception {


    /**
     * This Exception is supposed to be thrown whenever there is expected
     * to be some file path in the Includes structure, but it is not there.
     */
    private static final long serialVersionUID = 8334498811359471714L;
    private String filePath;

    /**
     * @param filePath
     */
    public MissingIncludeEntryException(String filePath) {
        this.filePath = filePath;
    }
    
    /**
     * Return the missing file path.
     * @return
     */
    public String getFilePath() {
        return filePath;
    }
    

}
