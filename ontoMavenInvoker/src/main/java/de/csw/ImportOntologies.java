package de.csw;

import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class ImportOntologies extends OntoMaven {

    public void setImportDirectory(String importDirectory){ configurations.put("importDirectory",importDirectory); }
    public String getImportDirectory(){ return configurations.get("importDirectory");}

    public void setCatalogFileName(String catalogFileName){ configurations.put("catalogFileName",catalogFileName); }
    public String getCatalogFileName(){ return configurations.get("catalogFileName");}

    public void setOwlDirectory(String owlDirectory){ configurations.put("owlDirectory",owlDirectory); }
    // sets the location of the import directory inside target/owl_target
    public void setOwlDirectoryInTarget(){ configurations.put("owlDirectory","${targetDir}"); }
    public String getOwlDirectory(){ return configurations.get("owlDirectory");}

    public void setOwlFileName(String owlFileName){ configurations.put("owlFileName",owlFileName); }
    public String getOwlFileName(){ return configurations.get("owlFileName");}

    public void setOwlFileURL(String owlFileURL){ configurations.put("owlFileURL",owlFileURL); }
    public String getOwlFileURL(){ return configurations.get("owlFileURL");}



    public void execute() throws IOException, MavenInvocationException {

        // set properties for import
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("project.build.sourceEncoding", "UTF-8");
        properties.put("targetDir", "${project.build.directory}/owl_target");

        //execute the maven goal
        executeGoal("de.csw:ontomaven:ImportOntologies", properties);
    }

    public static void main(String[] args) throws IOException, MavenInvocationException {
        System.out.println("Hello World!");
        ImportOntologies o = new ImportOntologies();
        o.setCatalogFileName("asdasd");
        o.setImportDirectory("asdasdawwwrhhhh");
        o.setOwlDirectory("tas");
        o.setOwlFileName("filyfile");
        o.setOwlFileURL("owl/pizza2.owl");
        o.setOwlDirectoryInTarget();
        o.addUserAspect("lololo");
        //o.printOutputOn();
        o.execute();

    }

}
