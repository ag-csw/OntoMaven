package de.csw;

import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.IOException;
import java.util.HashMap;

public class TestEntailment extends OntoMaven {


    public void setPremiseOntologyFileName(String premiseOntologyFileName){ configurations.put("premiseOntologyFileName",premiseOntologyFileName); }
    public String getPremiseOntologyFileName(){ return configurations.get("premiseOntologyFileName");}

    public void setConclusionOntologyFileName(String conclusionOntologyFileName){ configurations.put("conclusionOntologyFileName",conclusionOntologyFileName); }
    public String getConclusionOntologyFileName(){ return configurations.get("conclusionOntologyFileName");}

    public void setIfApplyPremiseAspects(String ifApplyPremiseAspects){ configurations.put("ifApplyPremiseAspects",ifApplyPremiseAspects); }
    public String getIfApplyPremiseAspects(){ return configurations.get("ifApplyPremiseAspects");}

    public void setIfApplyConclusionAspects(String ifApplyConclusionAspects){ configurations.put("ifApplyConclusionAspects",ifApplyConclusionAspects); }
    public String getIfApplyConclusionAspects(){ return configurations.get("ifApplyConclusionAspects");}

    public void setUserPremiseAspects(String userPremiseAspects){ configurations.put("userPremiseAspects",userPremiseAspects); }
    public String getUserPremiseAspects(){ return configurations.get("userPremiseAspects");}

    public void setUserConclusionAspects(String userConclusionAspects){ configurations.put("userConclusionAspects",userConclusionAspects); }
    public String getUserConclusionAspects(){ return configurations.get("userConclusionAspects");}

    public void setOwlDirectory(String owlDirectory){ configurations.put("owlDirectory",owlDirectory); }
    public String getOwlDirectory(){ return configurations.get("owlDirectory");}

    public void setAspectsIRI(String aspectsIRI){ configurations.put("aspectsIRI",aspectsIRI); }
    public String getAspectsIRI(){ return configurations.get("aspectsIRI");}



    public void execute() throws IOException, MavenInvocationException {

        //execute the maven goal
        executeGoal("de.csw:ontomaven:TestEntailment", new HashMap<String, String>());
    }
}
