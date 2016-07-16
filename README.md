OntoMaven
=========

Maven-based ontology development and management of ontology artifacts in distributed ontology repositories.

###User Guide (April 2016)

This document is for end users who are looking for information on how to run and use OntoMaven.

###### Prerequirements

<!--
TODO: check if you also need a local installation of git or svn to get access to maven:scm
-->

In order to run OntoMaven on your machine, you need:

- Mandatory is a **Java Virtual Machine (JVM)**.  Java 8 for 64-bit systems tested and found to be as a stable JVM for OntoMaven. Depending on your use case you may want to assign more RAM than assigned as default.
- An up-to-date **Apache Maven** version. At the time of creation of the guide at least Maven 3.0.5 was verified as a running version.
- If you want to use OntoMaven as a **Maven project inside Eclipse** or some other IDE you need their relevant Maven plugins. Please consider to use corresponding manuals for installation and usage.
- For support of distributed repositories you need to install a **source code management (SCM)** system like Git or Subversion (SVN).

<hr>

###### Installation and configuration details

<!--
TODO: perhaps the references to eclipse etc. are not necessary
-->

Before you start to use the instructions from this section, be sure you already fulfilled the prerequirements mentioned in the section before.

Since OntoMaven is an open source project, you can **get the source code** and build it on your own. You can retrieve the source code of OntoMaven on the project's site http://www.corporate-semantic-web.de/ontomaven.html.

You can **build and deploy OntoMaven** like usual for a Maven based application with the command `mvn build` on the command line or the corresponding command in your favorite IDE.

In order to **start the application** to import ontologies and to collaborate with other knowledge engineers and domain experts, your command should have the following form (parameters are optional):

`mvn de.csw:ontomaven:<goal_name> [-D<parameter_name>=<parameter_value>]`

In Eclipse or other similar IDE you can start goals from the given configuration menu to run applications. Here you should insert `de.csw:ontomaven:<goal_name>` into the menu field to define your goal.

The **available goals and their parameters** are listed in another section.

By default the support for **distributed repositories** is not active. Edit the pom.xml file in the root directory of OntoMaven to specify your ontology repository.

The following code snippet shows you how to connect with some repository server using SVN:

```xml
<project>
...
<scm>
	<connection>scm:svn:http://somerepository.com/svn_repo/trunk</connection>
	<developerConnection>scm:svn:https://somerepository.com/svn_repo/trunk</developerConnection>
	<url>http://somerepository.com/view.cvs</url>
</scm>
...
</project>
```

Instead of SVN you can connect to any other supported SCM.

<hr>

###### Supported SCM

As of this writing the following SCMs are fully supported:

- Bazaar
- CVS
- Git
- Mercurial
- Subversion
- ...

[Full list of supported SCMs (on the Apache Maven SCM site)](http://maven.apache.org/scm/scms-overview.html)

<hr>

###### Goals: Function and parameters

<!--
TODO:
1. complete the parameter's list for each goal
2. write a short overview of the function of each goal
3. perhaps rearrange the structure of the representation of the goals (from unordered list to sections)
-->

<!--
TODO: siehe Bachelorarbeit Anhang A, Seite 45ff
-->


* **ApplyAspects**<p>Applys the choosen aspects of the ontology and creates a new result ontology.</p>
	* `aspectsAppliedOwlDirectory` with default value `"target/aspectsAppliedOwlDirectory"`
	* `aspectsAppliedOwlFileName` with default value `"aspectsAppliedOntology.owl"`
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`
	* `userAspects` defined at the pom.xml file in the root directory of OntoMaven with the following lines:
    ```xml
    <userAspects>
        <aspect>myFirstAspect</aspect>
        <aspect>mySecondAspect</aspect>
        ...
    </userAspects>
    ```
* **CreateOntologyGraph**<p>Creates a graphml file of the ontology. The result file can be visualized by any tool supporting graphml files. Included into OntoMaven is also </p>
	* `graphDirectory` with default value `"target/site/graph"`
	* `graphFileName` with default value `"owlGraph.graphml"`
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`
	* `ifApplyAspects` with default value `false`
	* `userAspects` defined at the pom.xml file in the root directory of OntoMaven with the following lines:
	```xml
    <userAspects>
        <aspect>myFirstAspect</aspect>
        <aspect>mySecondAspect</aspect>
        ...
    </userAspects>
    ```
* **CreateOntologyReport**<p>Generates a report for the ontology. This report will contain information about the ontology and its elements like classes, properties, individuals.</p>
	* `ontologyReportOutputDirectory` with default value `"target/site/ontologyReport"`
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`
	* `ifApplyAspects` with default value `false`
	* `userAspects` defined at the pom.xml file in the root directory of OntoMaven with the following lines:
	```xml
    <userAspects>
        <aspect>myFirstAspect</aspect>
        <aspect>mySecondAspect</aspect>
        ...
    </userAspects>
    ```
* **ExportAxioms**<p>Exports the axioms of the ontology into a file. Furthermore aspects can be applied and inferred axioms can be printed and exported.</p>
	* `ifExportInferredAxioms` with default value `false`
	* `ifExportOriginalAxioms` with default value `true`
	* `axiomsExportDirectory` with default value `"target/exportedAxioms"`
	* `axiomsExportFileName` with default value `"exportedAxioms.txt"`
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`
	* `ifApplyAspects` with default value `false`
	* `userAspects` defined at the pom.xml file in the root directory of OntoMaven with the following lines:
	```xml
    <userAspects>
        <aspect>myFirstAspect</aspect>
        <aspect>mySecondAspect</aspect>
        ...
    </userAspects>
    ```
* **ImportOntologies**<p>Loads and saves all (also transitiv) imports. The loaded ontologies will be saved in a specific directory. Every imported ontology will also be registered in a catalog. All goals will work with the local ontologies, if they are imported before.</p>
	* `importDirectory` with default value `"imports"`
	* `catalogFileName` with default value `"catalog.xml"`
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`<p/>
* **InferAxioms**<p>Exports the inferred axioms of the ontology into a file.</p>
	* `ifIncludeOriginalAxioms` with default value `true`
	* `inferredOwlDirectory` with default value `"target/inferredOwlFiles"`
	* `inferredOwlFile` with default value `"inferredOntology.owl"`
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`
	* `ifApplyAspects` with default value `false`
	* `userAspects` defined at the pom.xml file in the root directory of OntoMaven with the following lines:
	```xml
    <userAspects>
        <aspect>myFirstAspect</aspect>
        <aspect>mySecondAspect</aspect>
        ...
    </userAspects>
    ```
* **PrintAspectNames**<p>Prints names of in a given ontology existing aspects.</p>
	* `owlDirectory` with default value `"owl"`
	* `owlFileName` with default value `"ontology.owl"`
	* `aspectsIRI` with default value `"http://www.corporate-semantic-web.de/ontologies/aspect_owl#hasAspect"`<p/>
* **RemoveAspects**<p>Applys the choosen aspects of the ontology and creates a new result ontology.</p>
	* `owlFileWithoutAspectsName` with default value `"ontologyWithoutAspects.owl"`
	* `owlFilesWithoutAspectsDirectory` with default value `"target/owlFilesWithoutAspects"`
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
	* `userAspects` defined at the pom.xml file in the root directory of OntoMaven with the following lines:
	```xml
    <userAspects>
        <aspect>myFirstAspect</aspect>
        <aspect>mySecondAspect</aspect>
        ...
    </userAspects>
    ```
