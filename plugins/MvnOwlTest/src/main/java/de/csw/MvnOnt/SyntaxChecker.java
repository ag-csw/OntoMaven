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
import com.hp.hpl.jena.ontology.tidy.Checker;
import com.hp.hpl.jena.ontology.tidy.SyntaxProblem;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.util.FileManager;

public class SyntaxChecker {
	
	
	
	
	protected List<String>  checker( String file_location){
		
		
		OntModel model = ModelFactory.createOntologyModel();
		

		List<String> message = new ArrayList<String>();
	       
	        
	        
	         try {
				
	        	 InputStream in = FileManager.get().open(file_location);
	        	              
	             model.read(in, "");
		
			} catch (Exception e) {
				// TODO: handle exception
				
				
				message.add("Ontology Create Problem");
			}
	        
	       boolean expecting = false;
       
       
	       Checker checker = new Checker(expecting);
	        
	        
	        checker.add(model);
	        
	        String subLang = checker.getSubLanguage();
	        
	        message.add(subLang);
	        
	      
	        
	        if(!(subLang.equals("Lite"))){
	        	
	        	Iterator it = checker.getProblems();
	        	
	        	while (it.hasNext()) {
	        		
					SyntaxProblem sp = (SyntaxProblem) it.next();
					
					//String s = sp.longDescription();
					
					String s = sp.shortDescription();
					
					//System.out.println(s);

					message.add(s);
				}
	        	
	        }
	        
	        return message;
		
	}

}
