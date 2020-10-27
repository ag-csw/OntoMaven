package de.csw;

import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.IOException;
import java.util.HashMap;

public class ExportAxioms extends OntoMaven {



    public void setIfExportInferredAxioms(String ifExportInferredAxioms){ configurations.put("ifExportInferredAxioms",ifExportInferredAxioms); }
    public String getIfExportInferredAxioms(){ return configurations.get("ifExportInferredAxioms");}

    public void setIfExportOriginalAxioms(String ifExportOriginalAxioms){ configurations.put("ifExportOriginalAxioms",ifExportOriginalAxioms); }
    public String getIfExportOriginalAxioms(){ return configurations.get("ifExportOriginalAxioms");}

    public void setAxiomsExportDirectory(String axiomsExportDirectory){ configurations.put("axiomsExportDirectory",axiomsExportDirectory); }
    public String getAxiomsExportDirectory(){ return configurations.get("axiomsExportDirectory");}

    public void setAxiomsExportFileName(String axiomsExportFileName){ configurations.put("axiomsExportFileName",axiomsExportFileName); }
    public String getAxiomsExportFileName(){ return configurations.get("axiomsExportFileName");}

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
        executeGoal("de.csw:ontomaven:ExportAxioms", new HashMap<String, String>());
    }
}
