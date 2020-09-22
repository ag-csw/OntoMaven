package de.csw;

import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.IOException;
import java.util.HashMap;

public class PrintAspectNames extends OntoMaven {

    PrintAspectNames(){
        super();
        //needUserAspects = true;
    }

    public void setOwlDirectory(String owlDirectory){ configurations.put("owlDirectory",owlDirectory); }
    public String getOwlDirectory(){ return configurations.get("owlDirectory");}

    public void setOwlFileName(String owlFileName){ configurations.put("owlFileName",owlFileName); }
    public String getOwlFileName(){ return configurations.get("owlFileName");}

    public void setAspectsIRI(String aspectsIRI){ configurations.put("aspectsIRI",aspectsIRI); }
    public String getAspectsIRI(){ return configurations.get("aspectsIRI");}



    public void execute() throws IOException, MavenInvocationException {

        //execute the maven goal
        executeGoal("de.csw:ontomaven:PrintAspectNames", new HashMap<String, String>());
    }
}
