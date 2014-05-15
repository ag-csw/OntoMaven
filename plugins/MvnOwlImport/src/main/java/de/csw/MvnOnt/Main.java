/*******************************************************************************
 * Copyright 2013 Corporate Semantic Web, Freie Universitaet Berlin
 * 
 * This file is part of the Coporate Semantic Web Project.
 * 
 * This work has been partially supported by the "InnoProfile-Corporate Semantic Web" project funded by the German Federal
 * Ministry of Education and Research (BMBF) and the BMBF Innovation Initiative for the New German Laender - Entrepreneurial Regions.
 * 
 * http://www.corporate-semantic-web.de/
 * 
 * 
 * Freie Universitaet Berlin
 * Copyright (c) 2007-2013
 * 
 * 
 * Institut fuer Informatik
 * Working Group Coporate Semantic Web
 * Koenigin-Luise-Strasse 24-26
 * 14195 Berlin
 * 
 * http://www.mi.fu-berlin.de/en/inf/groups/ag-csw/
 * 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.csw.MvnOnt;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 *
 * @goal owlimport
 * 
 * @phase process-sources
 */
public class Main
    extends AbstractMojo
{
    /**
     * Location of the ontology file.
     * @parameter 
	 * 		 expression="${owl}"
	 *		 default-value="src/resource/owl/pizza.owl"
     * @required
     */
	private String owlfile;
	
		
	/**
     * If the import files are stored
     * @parameter 
	 * 		expression="${local}"
     * 		default-value="false"
     */
	
    private boolean local;
    
    /**
     * Location of the import files, which are stored, and catalog.xml
     * @parameter 
     * 		expression="${catalog}"
	 * 		default-value="src/resource/catalog"
     */
	
    private String file_location;

    
    private List<IRI> iri;
    private List<String> succes_message;
    private List<String> miss_message;
    private List<String> message;
    
    public void execute()
        throws MojoExecutionException
    {
		//this.getLog().info(owlfile);
		
		System.out.println("--------------------------------------------------------------");
		System.out.println("**********  IMPORT OF ONTOLOGY  ******************************");
		System.out.println("--------------------------------------------------------------");
		
		File file = new File (owlfile);
    	URI url = null;
		Importer importer = null;
		iri = new ArrayList<IRI>();
    	try {
    		
    		this.getLog().info("Checking whether the import files can be imported ... ");
			//url = new URI(file.toURI().toString());
			url = file.toURI();
			//this.getLog().info(file.getAbsolutePath().toString());
			this.getLog().info(url.toString());
			importer = new Importer(url,file_location);
			iri = importer.all_Import();
			
		//} catch (URISyntaxException e) {
			
			//this.getLog().info("URISyntax Eror");
		
		} catch (OWLOntologyCreationException e) {
			
			this.getLog().info("");
		}
    	
    	succes_message = new ArrayList<String>();
		
		succes_message = importer.getSucces_import_messeage();
		
		if(!succes_message.isEmpty()){
			for (String message : succes_message) {
			
				//this.getLog().info(message);
				System.out.println(message);
			
			}
		}
		
		miss_message = new ArrayList<String>();
		
		miss_message = importer.getMiss_import_messeage();
		
		if(!miss_message.isEmpty()){
			
		
			for (String message : miss_message) {
			
				//this.getLog().info(message);
				System.out.println(message);
			}
		}
		
		this.getLog().info("Import part is finish");
		System.out.println("--------------------------------------------------------------");
		System.out.println("*****************  END OF IMPORT *****************************");
		System.out.println("--------------------------------------------------------------");
		
		if(local){
		
			System.out.println(" ");
			System.out.println("********** SAVE ONTOLOGY AND CREATE XMLCATALOG  *****************");
			System.out.println("   ");
			
			this.getLog().info("Try the import files to save");
			
			Loader loader = new Loader(iri, file_location);
			
			loader.loadTest();
			
			message = new ArrayList<String>();
			
			message = loader.getMessage();
			
			if(!message.isEmpty()){
			
				for (String getmessage : message) {
				
					//this.getLog().info(getmessage);	
					System.out.println(getmessage);
				}
			
			}
			
			this.getLog().info("done");
			
			System.out.println("--------------------------------------------------------------");
			System.out.println("********************  END ************************************");
			System.out.println("--------------------------------------------------------------");
		}
    }
}
