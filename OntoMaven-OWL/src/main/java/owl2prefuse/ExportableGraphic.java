package owl2prefuse;

import java.io.File;

/**
 * All classes that implement this interface indicate they can be exported to a 
 * graphical image.
 * 
 * <p/>
 * Project OWL2Prefuse <br/>
 * ExportableGraphic.java created 10 januari 2007, 10:35
 * <p/>
 * Copyright &copy 2006 Jethro Borsje
 * 
 * 
 * @author <a href="mailto:info@jborsje.nl">Jethro Borsje</a>
 * @version $$Revision:$$, $$Date:$$
 */
public interface ExportableGraphic
{
    /**
     * Export the display of the object which implements this interface, to the 
     * given file type.
     * @param p_file The file which is going to contain the export, valid file types 
     * are: "png" and "jpg".
     * @param p_fileType The file type of the image to be created.
     */
    void export(File p_file, String p_fileType);
}