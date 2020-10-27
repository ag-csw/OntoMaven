package de.csw;

import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.IOException;
import java.util.HashMap;

public class InferAxioms extends OntoMaven {


    public void setIfIncludeOriginalAxioms(String ifIncludeOriginalAxioms){ configurations.put("ifIncludeOriginalAxioms",ifIncludeOriginalAxioms); }
    public String getIfIncludeOriginalAxioms(){ return configurations.get("ifIncludeOriginalAxioms");}

    public void setInferredOwlDirectory(String inferredOwlDirectory){ configurations.put("inferredOwlDirectory",inferredOwlDirectory); }
    public String getInferredOwlDirectory(){ return configurations.get("inferredOwlDirectory");}

    public void setInferredOwlFile(String inferredOwlFile){ configurations.put("inferredOwlFile",inferredOwlFile); }
    public String getInferredOwlFile(){ return configurations.get("inferredOwlFile");}

    public void setOwlDirectory(String owlDirectory){ configurations.put("owlDirectory",owlDirectory); }
    public String getOwlDirectory(){ return configurations.get("owlDirectory");}

    public void setOwlFileName(String owlFileName){ configurations.put("owlFileName",owlFileName); }
    public String getOwlFileName(){ return configurations.get("owlFileName");}

    public void setAspectsIRI(String aspectsIRI){ configurations.put("aspectsIRI",aspectsIRI); }
    public String getAspectsIRI(){ return configurations.get("aspectsIRI");}

    public void setIfApplyAspects(String ifApplyAspects){ configurations.put("ifApplyAspects",ifApplyAspects); }
    public String getIfApplyAspects(){ return configurations.get("ifApplyAspects");}

    public void setKeepNonAspectAxioms(String keepNonAspectAxioms){configurations.put("keepNonAspectAxioms", keepNonAspectAxioms);}



    public void execute() throws IOException, MavenInvocationException {

        //execute the maven goal
        executeGoal("de.csw:ontomaven:InferAxioms", new HashMap<String, String>());
    }
}
