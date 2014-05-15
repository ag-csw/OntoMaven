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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.util.FileManager;

public class ConsistentChecker {
	
	protected List<String> check(String file_location){
		
		
		OntModel model = ModelFactory.createOntologyModel();
		

		List<String> message = new ArrayList<String>();
		
	       
        
        
        try {
			
       	 InputStream in = FileManager.get().open(file_location);
       	              
            model.read(in, "");
	
		} catch (Exception e) {
			// TODO: handle exception
			
			message.add("Ontology Create Problem");
		}
        
        ValidityReport valid = model.validate();
        
        if(valid.isValid()){
        	
    
        	
        	message.add("OK ----> The Ontology is consistent");
        	
        }else {
			
        	System.out.println("Conflicts ----> the Ontology is inconsistent");
        	
        	message.add("Conflicts");
        	
        	for (Iterator it = valid.getReports(); it.hasNext();) {
        		
				ValidityReport.Report report = (ValidityReport.Report)it.next();
				
				//System.out.println("-"+report);
				
				message.add(report.toString());
			}
        	
		}
        
		
		return message;
	}

}
