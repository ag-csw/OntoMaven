package de.csw;

import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class ApplyAspects extends OntoMaven {


    // default value "target/aspectsAppliedOwlDirectory"
    public void setAspectsAppliedOwlDirectory(String dirName){ configurations.put("aspectsAppliedOwlDirectory",dirName); }
    public String getAspectsAppliedOwlDirectory(){ return configurations.get("aspectsAppliedOwlDirectory");}

    // default value "aspectsAppliedOntology.owl"
    public void setAspectsAppliedOwlFileName(String fileName){ configurations.put("aspectsAppliedOwlFileName",fileName); }
    public String getAspectsAppliedOwlFileName(){ return configurations.get("aspectsAppliedOwlFileName");}

    // default value "owl"
    public void setOwlDirectory(String owlDirectory){ configurations.put("owlDirectory",owlDirectory); }
    public String getOwlDirectory(){ return configurations.get("owlDirectory");}

    // default value "ontology.owl"
    public void setOwlFileName(String owlFileName){ configurations.put("owlFileName",owlFileName); }
    public String getOwlFileName(){ return configurations.get("owlFileName");}

    // default value "http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"
    public void setAspectsIRI(String aspectsIRI){ configurations.put("aspectsIRI",aspectsIRI); }
    public String getAspectsIRI(){ return configurations.get("aspectsIRI");}

    public void setOwlFileURL(String owlFileURL){ configurations.put("owlFileURL",owlFileURL); }
    public String getOwlFileURL(){ return configurations.get("owlFileURL");}



    public void execute() throws IOException, MavenInvocationException {

        //execute the maven goal
        executeGoal("de.csw:ontomaven:ApplyAspects", new HashMap<String, String>());
    }

}
