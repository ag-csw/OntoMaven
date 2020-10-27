package org.example;
import de.csw.*;
import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Hello world!
 *
 */
public class Example
{

    public static void main( String[] args ) throws IOException, MavenInvocationException {

        // set the directory where the ontology should be imported
        HashMap<String, String> basicConfig = new HashMap<String, String>();
        basicConfig.put("owlDirectory","owl");
        basicConfig.put("owlFileName","aspect-example.owl");



        //////////////////////// Import Ontologies: aspect-example.owl  ///////////////////////////
        ImportOntologies impOnt = new ImportOntologies();
        // set the basic configurations
        impOnt.overrideConfigurations((HashMap<String, String>) basicConfig.clone());
        // set the fileURL of the ontology that should be imported from inside the resources folder
        impOnt.setOwlFileURL("owl/aspect-example.owl");
        // import the ontology
        impOnt.addConfiguration("forceRefresh","true");
        impOnt.printOutputOn();
        impOnt.execute();



        ///////////////////// Apply Aspects ///////////////////////////
        ApplyAspects applAsp = new ApplyAspects();
        applAsp.printOutputOn();
        // set the basic configurations (location where the base ontology is in)
        applAsp.overrideConfigurations((HashMap<String, String>) basicConfig.clone());
        // set the directory of the result ontology
        applAsp.setAspectsAppliedOwlDirectory("owl/aspect-example");
        // add the aspects that should be applied
        applAsp.addUserAspect("http://ontology.aspectowl.xyz/untitled-ontology-280#Student");
        // apply aspects
        applAsp.execute();


        /////////////////// CreateOntologyGraph with certian aspects //////////////////////
        CreateOntologyGraph createOntologyGraph = new CreateOntologyGraph();
        // set the basic configurations (location where the base ontology is in)
        createOntologyGraph.overrideConfigurations((HashMap<String, String>) basicConfig.clone());
        // add userAspect if wanted
        createOntologyGraph.setIfApplyAspects("true");
        createOntologyGraph.addUserAspect("http://ontology.aspectowl.xyz/untitled-ontology-280#Student");
        // create ontology graph ( because not specified it can be found in default directory: target/site/graph/owlGraph.graphml)
        createOntologyGraph.execute();


        /**
        //////////////// CreateOntologyReport /////////////////////////////
        CreateOntologyReport createOntologyReport = new CreateOntologyReport();
        // set the basic configurations (location where the base ontology is in)
        createOntologyReport.overrideConfigurations(basicConfig);
        // add userAspect if wanted
        createOntologyReport.setIfApplyAspects("true");
        createOntologyReport.addUserAspect("http://ontology.aspectowl.xyz/untitled-ontology-280#Student");
        // create ontology report (because not specified it can be found in default direcrory: target/site/ontologyReport)
        createOntologyReport.execute();
         **/



        ////////////// ExportAxioms /////////////////
        ExportAxioms exportAxioms = new ExportAxioms();
        // set the basic configurations (location where the base ontology is in)
        exportAxioms.overrideConfigurations((HashMap<String, String>) basicConfig.clone());
        // set configuration that infered axioms shouldnt be exported and that original axioms should be exported (this would be the default setting anyway)
        exportAxioms.setIfExportInferredAxioms("false");
        exportAxioms.setIfExportOriginalAxioms("true");
        // set if filtered by aspects and set aspects (default no aspects)
        exportAxioms.setIfApplyAspects("true");
        exportAxioms.addUserAspect("http://ontology.aspectowl.xyz/untitled-ontology-280#Student");
        // export axioms (default location: target/exportedAxioms/exportedAxioms.txt)
        exportAxioms.execute();



        /////////////// InferAxioms //////////////
        InferAxioms inferAxioms = new InferAxioms();
        // set the basic configurations (location where the base ontology is in)
        inferAxioms.overrideConfigurations((HashMap<String, String>) basicConfig.clone());
        // only export infered axioms
        inferAxioms.setIfIncludeOriginalAxioms("true");
        inferAxioms.setKeepNonAspectAxioms("false");
        // inferAxioms.setIfApplyAspects("false");
        //inferAxioms.addUserAspect("http://ontology.aspectowl.xyz/untitled-ontology-280#Student");
        // infer axioms and write them to a file (default location: target/inferredOwlFiles/inferredOntology.owl)
        inferAxioms.execute();




        //////////// PrintAspectNames /////////////////
        PrintAspectNames printAspectNames = new PrintAspectNames();
        // set the basic configurations (location where the base ontology is in)
        printAspectNames.overrideConfigurations((HashMap<String, String>) basicConfig.clone());
        // print out
        printAspectNames.execute();


        /////////////////// RemoveAspects ///////////////////////
        RemoveAspects removeAspects = new RemoveAspects();
        // set the basic configurations (location where the base ontology is in)
        removeAspects.overrideConfigurations((HashMap<String, String>) basicConfig.clone());
        // export ontology without aspects (default location: target/owlFilesWithoutAspects/ontologyWithoutAspects.owl)
        removeAspects.execute();



        //////////////// TestOntology //////////////////
        TestOntology testOntology = new TestOntology();
        testOntology.overrideConfigurations((HashMap<String, String>) basicConfig.clone());
        testOntology.execute();

    }
}
