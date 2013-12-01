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

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class CompareOntologies {
	
	
	private String owl_orginalfile;
	private String owl_file; 
	
	private final String commandTyp_diff = "diff";
	private final String commandTyp_status = "status";
	
	
	public CompareOntologies(String owl_orginalfile, String owl_file){
		
		this.owl_orginalfile = owl_orginalfile;
		
		this.owl_file = owl_file;
		
		
	}
	
	public String load_compare(String commandTyp){
		
	
		
		String result= null;
		
		try{		
			
			OWLOntologyManager manager1 = OWLManager.createOWLOntologyManager();
			OWLOntologyManager manager2 = OWLManager.createOWLOntologyManager();
			
		
			
			URL uri_orginal = new URL("file:///"+owl_orginalfile);
			URL uri = new URL("file:///"+owl_file);
			

			
			IRI ontology1IRI = IRI.create(uri_orginal);
			IRI ontology2IRI = IRI.create(uri);

			
			
			LoadOntology loader1 = new LoadOntology(manager1, ontology1IRI);
			OWLOntology orginal_ontology = loader1.loadOntology();	
			//System.out.println("loading ontology 1 complete");
			
			
			
			
			LoadOntology loader2 = new LoadOntology(manager2, ontology2IRI);
			OWLOntology ontology = loader2.loadOntology();	
			//System.out.println("loading ontology 2 complete");
			
			
			if(commandTyp.equals(commandTyp_status)){
			
				if(isSame(orginal_ontology, ontology)){
					
					result = "Ontology is same";
						
				}else{
					
					result = "Ontology changed";
					//change = true;
				
				}
			
			}
			
			if(commandTyp.equals(commandTyp_diff)){
				
				System.out.println("Ontology File : "+owl_file);
				
				if(!isSame(orginal_ontology, ontology)){
					
					outDiff(orginal_ontology, ontology);
		
					
				}else {
					
					result = "Ontology is same";
				}
				
			}
		
		
		}
		catch(Exception e){
			//System.out.println("An error occured during intial ontology loading: " + e.printStackTrace());
			
			e.printStackTrace();
		}
		

		
		
		return result;
		
	}
	
	
//-------------------------------------------------------------------------------------------------------------------------------------------------------	
//--------------------------------------------------------------------------------------------------------------------------------------------------------	
	
	private boolean isSame(OWLOntology orginal_ontology, OWLOntology ontology){
		
 
		boolean isEqual;

		
		
		if(!search(orginal_ontology, ontology).isEmpty() || !search(ontology, orginal_ontology).isEmpty()){
			
			isEqual = false;
			
		}else {
			isEqual = true;
		}
		
		return isEqual;	
	}
	
	
//----------------------------------------------------------------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------------------------------------------------------	
	
	private void outDiff(OWLOntology orginal_ontology, OWLOntology ontology){
		

		
		List<OWLAxiom> diff = new ArrayList<OWLAxiom>();
		List<OWLAxiom> diff2 = new ArrayList<OWLAxiom>();
		
		System.out.println("             ");
		
		diff = search(orginal_ontology, ontology);
		
		diff2 = search(ontology, orginal_ontology);
		
		
//		if(diff.isEmpty()){
//			
//			System.out.println("only working copy changed--> next step should be 'commit' ");
//			
//		}
		
			
		
		System.out.println("======================      ACTUAL CHANGES      ===========================");
		
		
		if(!diff.isEmpty() && diff2.isEmpty()){
		
			System.out.println("---> Axioms were added to the repository, or deleted from the working copy.");
			System.out.println("  ");
			
			for(OWLAxiom ax : diff){
				
				System.out.println(ax);
				
			}
			
		}
		
		if(diff.isEmpty() && !diff2.isEmpty()){
			
			System.out.println("---> Axioms were added to the working copy, or deleted from the repository.");
			
			for(OWLAxiom ax : diff2){
				
				System.out.println(ax);
			}
			
			System.out.println("only working copy changed--> next step should be 'commit' ");
			
		}
		
		
		if(!diff.isEmpty() && !diff2.isEmpty()){
			
			System.out.println("---> Axioms were added to the repository and working copy, or deleted.");
			
			System.out.println(" ");
			System.out.println("----> Changes from the perspective of repository");
			
			for(OWLAxiom ax : diff){
				
				System.out.println(ax);
				
			}
			
			System.out.println("----> Changes from the perspective of working copy");
			
			for(OWLAxiom ax : diff2){
				
				System.out.println(ax);
			}
			
		}
		
		
		System.out.println("================================================================");
		
		diff.addAll(diff2);
		
		
		
		Set<OWLClassExpression> oclass = new HashSet<OWLClassExpression>();
		
		for (OWLAxiom owlAxiom : diff) {
			
			
				for(OWLClassExpression cd : owlAxiom.getClassesInSignature()){
					
					oclass.add(cd);
					
				}
		
			}
			
		
		//----------------------------------------------------------------
		System.out.println("   ");
		System.out.println("------------------------    MORE INFO    -----------------------");
		System.out.println("---> The above changes of the OWL classes are dependent on the following axiom.");
		System.out.println("   ");
		
		if(!oclass.isEmpty()){
			
			for(OWLClassExpression cls : oclass){
				
				if(!cls.isOWLThing()){
				
				//System.out.println(cls);	
					
					
					for(OWLClassExpression ce : cls.asOWLClass().getSubClasses(orginal_ontology)){
						
						//if(!cls.equals(ce)){
						
							System.out.println(ce.asOWLClass().getIRI().getFragment()+"  <------ Subklasse");
						
						//}
						
					}
					
					
					for(OWLDataProperty dat : ontology.getDataPropertiesInSignature()){
						
						for (OWLClassExpression ex : dat.getDomains(orginal_ontology)) {
							
							if(ex.equals(cls)){
								
								//System.out.println(ex);
								
								System.out.println(dat.getIRI().getFragment()+"  <------ DataProperty (Domain)");
								
								
							}
							
						}
						
					}
					
					for(OWLObjectProperty ob : ontology.getObjectPropertiesInSignature()){
						
						for(OWLClassExpression ex : ob.getDomains(orginal_ontology)){
							
							if(ex.equals(cls)){
								
								System.out.println(ob.getIRI().getFragment()+"  <------ ObjectProperty (Domain)");
								
								//System.out.println(ex);
							}
							
						}
						
						for(OWLClassExpression ex : ob.getRanges(orginal_ontology)){
							
							if(ex.equals(cls)){
								
								System.out.println(ob.getIRI().getFragment()+"  <------ ObjectProperty (Range)");
								
								//System.out.println(ex);
							}
						}
						
					}
		
					
				}
				
			}
			
		}	
		
		System.out.println("------------------------------------------------------------");
		System.out.println("------------------------------------------------------------");	
		System.out.println("   ");
		//return true;
	}
	
	
	
	
	private List<OWLAxiom> search(OWLOntology ont1, OWLOntology ont2){
		
		
		Set<OWLAxiom> list = new HashSet<OWLAxiom>();
		
		Set<OWLAxiom> list_copy = new HashSet<OWLAxiom>();
		
		List<OWLAxiom> diff = new ArrayList<OWLAxiom>();
		
		
		
		for (OWLAxiom cls : ont1.getAxioms()) {
			
			
			list.add(cls);
		}
		
		for (OWLAxiom cls : ont2.getAxioms()) {
			
			
			list_copy.add(cls);
			
		}
		
		boolean test = false;

		
		for (OWLAxiom owlAxiom : list) {
			
			test=true;
				
			for (OWLAxiom axiom : list_copy) {
					
				if(owlAxiom.equals(axiom)){

					test = false;
					break;
						
				}
					
			}
				
			if(test){
					
					
				diff.add(owlAxiom);
				
			}
				
		}
		
		
		return diff;
	}
	
	
	
	

}
