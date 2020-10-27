package de.csw;

import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.IOException;
import java.util.HashMap;

public class CreateOntologyReport extends OntoMaven {



    public void setOntologyReportOutputDirectory(String ontologyReportOutputDirectory){ configurations.put("ontologyReportOutputDirectory",ontologyReportOutputDirectory); }
    public String getOntologyReportOutputDirectory(){ return configurations.get("ontologyReportOutputDirectory");}

    public void setOwlDirectory(String owlDirectory){ configurations.put("owlDirectory",owlDirectory); }
    public String getOwlDirectory(){ return configurations.get("owlDirectory");}

    public void setOwlFileName(String owlFileName){ configurations.put("owlFileName",owlFileName); }
    public String getOwlFileName(){ return configurations.get("owlFileName");}

    public void setAspectsIRI(String aspectsIRI){ configurations.put("aspectsIRI",aspectsIRI); }
    public String getAspectsIRI(){ return configurations.get("aspectsIRI");}

    public void setIfApplyAspects(String ifApplyAspects){ configurations.put("ifApplyAspects",ifApplyAspects); }
    public String getIfApplyAspects(){ return configurations.get("ifApplyAspects");}



    public void execute() throws IOException, MavenInvocationException {

        //execute the maven goal
        executeGoal("de.csw:ontomaven:CreateOntologyReport", new HashMap<String, String>());
    }
}
