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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.mindswap.pellet.jena.PelletReasonerFactory;

import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;

public class EntailmentCheck {
	
	
	
	

	protected String checkEntailment( File premiseOntologyFile, File conclusionOntologyFile)
	       throws Exception {


		String result = null;   
		Model premiseOntologyModel = readModel(premiseOntologyFile);
              
		Model conclusionOntologyModel = readModel(conclusionOntologyFile);
              
		InfModel theReasonerModel = createPelletReasonerModel(premiseOntologyModel);

		if (theReasonerModel.containsAll(conclusionOntologyModel)) {
  
			//System.out.println("|----------          RESULT=ENTAILMENT          ----------|");
			
			result = "RESULT=ENTAILMENT";
      
		} else {
                      
			//System.out.println("|----------          RESULT=NONENTAILMENT           ------|");
              
			result = "RESULT=NONENTAILMENT ";  
		}
		
		return result;
	}



  
private InfModel createPelletReasonerModel(Model ontologyModel) throws Exception {




		Reasoner reasoner =  (Reasoner) PelletReasonerFactory.theInstance().create();




		InfModel result = ModelFactory.createInfModel(reasoner,ontologyModel);

		result.prepare();      
   
 	return result;
}



private Model readModel(File rdfFile) throws FileNotFoundException{


Model result = ModelFactory.createDefaultModel().read( new FileInputStream(rdfFile), /*base=*/null,"RDF/XML" );


return result;


}

}
