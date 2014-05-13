package owl2prefuse;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.util.FileManager;
import java.io.InputStream;

/**
 * This class is the super class of specific converter classes.
 * <p/>
 * Project OWL2Prefuse <br/>
 * Converter.java created 2 januari 2007, 10:41
 * <p/>
 * Copyright &copy 2006 Jethro Borsje
 * @author <a href="mailto:info@jborsje.nl">Jethro Borsje</a>
 * @version $$Revision:$$, $$Date:$$
 */
public class Converter
{
    /**
     * The Jena model that needs to be converted to Prefuse.
     */
    protected OntModel m_model;
    
    /**
     * Creates a new instance of Converter.
     * @param p_OWLFile The path to the OWL file which needs to be converted.
     */
    public Converter(String p_OWLFile)
    {
        load(p_OWLFile);
        
        // Normalize the model.
        OWLNormalize();
    }
    
    /**
     * Creates a new instance of Converter.
     * @param p_model The Jena OntModel that needs to be converted.
     */
    public Converter(OntModel p_model)
    {
        m_model = p_model;
        
        // Normalize the model.
        OWLNormalize();
    }
    
    /**
     * Get all the classes inthe ontology which do not have a superclass and link
     * them to owl:Thing.
     */
    protected void OWLNormalize()
    {
        // The SPARQL query.
        String strQuery = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>" +
                "SELECT ?x" +
                "{" +
                "?x rdf:type owl:Class ." +
                "OPTIONAL { ?x rdfs:subClassOf ?y }" +
                "FILTER (!bound(?y))" +
                "FILTER (!isBlank(?x))" +
                "}";
        
        Query query = QueryFactory.create(strQuery);
        QueryExecution qexec = QueryExecutionFactory.create(query, m_model);
        ResultSet rs = qexec.execSelect();
  
        OntClass rootClass = m_model.getOntClass("http://www.w3.org/2002/07/owl#Thing");
        Property subClassProp = m_model.getProperty("http://www.w3.org/2000/01/rdf-schema#subClassOf");
        
        while (rs.hasNext())
        {
            Resource resource = rs.nextSolution().getResource("x");
            resource.addProperty(subClassProp, rootClass);
        }
    }
    
    /**
     * Import the OWL database file into the Jena ontology model.
     * @param p_OWLFile The path to the OWL file which needs to be converted.
     */
    private void load(String p_OWLFile)
    {
        m_model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        
        InputStream in = FileManager.get().open(p_OWLFile);
        if (in == null)
        {
            throw new IllegalArgumentException("File: " + p_OWLFile + " not found");
        }
        
        m_model.read(in, "");
    }
}