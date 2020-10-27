OntoMavenInvoker
=========

Invokes Maven goals of OntoMaven

###### Prerequirements

- Mandatory is a **Java Virtual Machine (JVM)**.  Java 11 for 64-bit systems tested and found to be as a stable JVM for OntoMaven. Depending on your use case you may want to assign more RAM than assigned as default.
- An up-to-date **Apache Maven** version.
- you need to install the ontoMaven repository to your maven home repository, you can find it here: https://gitlab.fokus.fraunhofer.de/rsc1/OntoMaven/-/tree/ontoMavenInvoker/OntoMaven-OWL.
<hr>



###### Usage and Example

To invoke OntoMaven goals you need to first define the associated configurations. They just work like in the OntoMaven plugin. Here is an example of how to invoke the Maven goals ImportOntologies and ApplyAspects. You can find other examples here: https://gitlab.fokus.fraunhofer.de/rsc1/OntoMaven/-/tree/ontoMavenInvoker/OntoMaven-Examples <br> <br>

First we need to set the directory where to import the ontologies and work with them afterwards. We set the directory to "owl" and the ontology filename to "aspect-example.owl". After the import we can find the file in the target folder under "owl/aspect-example.owl"
```
// set the directory where the ontology should be imported
HashMap<String, String> basicConfig = new HashMap<String, String>();
basicConfig.put("owlDirectory","owl");
basicConfig.put("owlFileName","aspect-example.owl");
```
We wrote the basic configurations to the Hashmap basicConfig. Now we will import the ontology. for that we will first set the basic informaition via the overrideConfigurations method (we could also do it with the setOwlDirectory and setOwlFileName method or with addConfiguration). Then we will define the location in the src/resources folder of the ontology that we want to import, in this case "owl/aspect-example.owl". With printOutputOn the OntoMaven messages will be printed to System.out and finally we execute the goal with impOnt.execute(). 

```
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

```
Now we also invoke the ApplyAspects goal. This works similar like ImportOntologies but we also need to define aspects with the addUserAspects method.
```
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
```

Here is a list of all of the goals and most of there configurations. You can set the configuration either all with a Hashmap and use the overrideConfigurations method or you set every configuration with its set method, e.g. setOwlDirectory for owlDirectory configuration or you just add it like addConfiguration("owlDirectory","owl"). For goals which use aspects you add them with the addUserAspect method.

* **ApplyAspects**<p>Applys the choosen aspects of the ontology and creates a new result ontology.</p>
	* `aspectsAppliedOwlDirectory` with default value `"aspectsAppliedOwlDirectory"`
	* `aspectsAppliedOwlFileName` with default value `"aspectsAppliedOntology.owl"`
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`
	* `userAspects` add them with the addUserAspect method
* **CreateOntologyGraph**<p>Creates a graphml file of the ontology. The result file can be visualized by any tool supporting graphml files. Included into OntoMaven is also </p>
	* `graphDirectory` with default value `"site/graph"`
	* `graphFileName` with default value `"owlGraph.graphml"`
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`
	* `ifApplyAspects` with default value `false`
	* `userAspects` add them with the addUserAspect method
* **CreateOntologyReport**<p>Generates a report for the ontology. This report will contain information about the ontology and its elements like classes, properties, individuals.</p>
	* `ontologyReportOutputDirectory` with default value `"site/ontologyReport"`
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`
	* `ifApplyAspects` with default value `false`
	* `userAspects` add them with the addUserAspect method
* **ExportAxioms**<p>Exports the axioms of the ontology into a file. Furthermore aspects can be applied and inferred axioms can be printed and exported.</p>
	* `ifExportInferredAxioms` with default value `false`
	* `ifExportOriginalAxioms` with default value `true`
	* `axiomsExportDirectory` with default value `"exportedAxioms"`
	* `axiomsExportFileName` with default value `"exportedAxioms.txt"`
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`
	* `ifApplyAspects` with default value `false`
	* `userAspects` add them with the addUserAspect method
* **ImportOntologies**<p>Loads and saves all (also transitiv) imports. The loaded ontologies will be saved in a specific directory. Every imported ontology will also be registered in a catalog. All goals will work with the local ontologies, if they are imported before.</p>
	* `importDirectory` with default value `"imports"`
	* `catalogFileName` with default value `"catalog.xml"`
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`<p/>
* **InferAxioms**<p>Exports the inferred axioms of the ontology into a file.</p>
	* `ifIncludeOriginalAxioms` with default value `true`
	* `inferredOwlDirectory` with default value `"inferredOwlFiles"`
	* `inferredOwlFile` with default value `"inferredOntology.owl"`
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`
	* `ifApplyAspects` with default value `false`
	* `userAspects` add them with the addUserAspect method
* **PrintAspectNames**<p>Prints names of in a given ontology existing aspects.</p>
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`<p/>
* **RemoveAspects**<p>Applys the choosen aspects of the ontology and creates a new result ontology.</p>
	* `owlFileWithoutAspectsName` with default value `"ontologyWithoutAspects.owl"`
	* `owlFilesWithoutAspectsDirectory` with default value `"owlFilesWithoutAspects"`
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`<p/>
* **TestEntailment**<p>Tests if the axioms of an ontology can be inferred from the axioms of another ontology.</p>
	* `premiseOntologyFileName` with default value `"premiseOntology.owl"`
	* `conclusionOntologyFileName` with default value `"conclusionOntology.owl"`
	* `ifApplyPremiseAspects` with default value `true`
	* `ifApplyConclusionAspects` with default value `true`
	* `userPremiseAspects` with default value `[]` (empty string array)
	* `userConclusionAspects` with default value `[]` (empty string array)
	* `owlDirectory` with default value `"owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`<p/>
* **TestOntology**<p>Tests an ontology regarding the syntax and consistency. The result will be printed in the console and written into a specified file.</p>
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`
	* `ifApplyAspects` with default value `false`
	* `userAspects` add them with the addUserAspect method
