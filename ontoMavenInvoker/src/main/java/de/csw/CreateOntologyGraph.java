package de.csw;

import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.IOException;
import java.util.HashMap;


public class CreateOntologyGraph extends OntoMaven {

    CreateOntologyGraph(){
        super();
        //needUserAspects = true;
    }

    public void setGraphDirectory(String graphDirectory){ configurations.put("graphDirectory",graphDirectory); }
    public String getGraphDirectory(){ return configurations.get("graphDirectory");}

    public void setGraphFileName(String graphFileName){ configurations.put("graphFileName",graphFileName); }
    public String getGraphFileName(){ return configurations.get("graphFileName");}

    // default value "owl"
    public void setOwlDirectory(String owlDirectory){ configurations.put("owlDirectory",owlDirectory); }
    public String getOwlDirectory(){ return configurations.get("owlDirectory");}

    // default value "ontology.owl"
    public void setOwlFileName(String owlFileName){ configurations.put("owlFileName",owlFileName); }
    public String getOwlFileName(){ return configurations.get("owlFileName");}

    // default value "http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"
    public void setAspectsIRI(String aspectsIRI){ configurations.put("aspectsIRI",aspectsIRI); }
    public String getAspectsIRI(){ return configurations.get("aspectsIRI");}

    public void setIfApplyAspects(String ifApplyAspects){ configurations.put("ifApplyAspects",ifApplyAspects); }
    public String getIfApplyAspects(){ return configurations.get("ifApplyAspects");}

    public void execute() throws IOException, MavenInvocationException {

        //execute the maven goal
        executeGoal("de.csw:ontomaven:CreateOntologyGraph", new HashMap<String, String>());
    }
}
