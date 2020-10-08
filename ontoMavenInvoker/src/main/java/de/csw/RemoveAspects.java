package de.csw;

import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.IOException;
import java.util.HashMap;

public class RemoveAspects extends OntoMaven {


    public void setOwlFileWithoutAspectsName(String owlFileWithoutAspectsName){ configurations.put("owlFileWithoutAspectsName",owlFileWithoutAspectsName); }
    public String getOwlFileWithoutAspectsName(){ return configurations.get("owlFileWithoutAspectsName");}

    public void setOwlFilesWithoutAspectsDirectory(String owlFilesWithoutAspectsDirectory){ configurations.put("owlFilesWithoutAspectsDirectory",owlFilesWithoutAspectsDirectory); }
    public String getOwlFilesWithoutAspectsDirectory(){ return configurations.get("owlFilesWithoutAspectsDirectory");}

    public void setOwlDirectory(String owlDirectory){ configurations.put("owlDirectory",owlDirectory); }
    public String getOwlDirectory(){ return configurations.get("owlDirectory");}

    public void setOwlFileName(String owlFileName){ configurations.put("owlFileName",owlFileName); }
    public String getOwlFileName(){ return configurations.get("owlFileName");}

    public void setAspectsIRI(String aspectsIRI){ configurations.put("aspectsIRI",aspectsIRI); }
    public String getAspectsIRI(){ return configurations.get("aspectsIRI");}



    public void execute() throws IOException, MavenInvocationException {

        //execute the maven goal
        executeGoal("de.csw:ontomaven:RemoveAspects", new HashMap<String, String>());
    }
}
