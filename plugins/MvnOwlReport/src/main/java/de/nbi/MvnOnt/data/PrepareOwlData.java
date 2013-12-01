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
package de.nbi.MvnOnt.data;




import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.UnknownOWLOntologyException;
import org.semanticweb.owlapi.profiles.OWL2DLProfile;
import org.semanticweb.owlapi.profiles.OWL2ELProfile;
import org.semanticweb.owlapi.profiles.OWL2Profile;
import org.semanticweb.owlapi.profiles.OWL2QLProfile;
import org.semanticweb.owlapi.profiles.OWL2RLProfile;

public class PrepareOwlData {

	
	private OWLOntology ontology;
	private OWLOntologyManager manager; 
	
	public PrepareOwlData(String owl_file){
		
		//this.location=owl_file;
		
		owl_file = "owl/camera.owl";
		
		File file = new File(owl_file);
		
		this.manager = OWLManager.createOWLOntologyManager();
		
		try {
			
			System.out.println("start to load");
			this.ontology = manager.loadOntologyFromOntologyDocument(file);
			System.out.println("loaded");
			
		} catch (OWLOntologyCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	public String getOntID(){
				
		return ontology.getOntologyID().toString();
		
	}
	
	//public Set<OWLAnnotation> getComment(){
	public String getComment(){	

		String comment = "";
		
		for (OWLAnnotation it : ontology.getAnnotations()) {
			
			comment+=it.getValue().toString()+" ";
		}
		
		//return ontology.getAnnotations();
		
		return comment;
		
		
	}
	
	
	
	public String getOntologyFormat(){
		try {
			
			
			return manager.getOntologyFormat(ontology).toString();
			
		} catch (UnknownOWLOntologyException e) {
			// TODO: handle exception
			
			return "unknown format";
		}
		
		
	}
	
	public String getProfile(){
		

		OWL2Profile prof = new OWL2Profile();
		OWL2DLProfile profdl = new OWL2DLProfile();
		
		OWL2ELProfile profel = new  OWL2ELProfile();
		OWL2QLProfile profql = new OWL2QLProfile();
		OWL2RLProfile profrl = new OWL2RLProfile();
		   	
    	String onto_profil = null;

    	
    	if (profdl.checkOntology(ontology).isInProfile()) {
    		
    		
    		onto_profil = "The Ontology is in "+profdl.getName();
    					
		}
    	
    	
    	if (profel.checkOntology(ontology).isInProfile()) {
    		
    		if(!onto_profil.equals(null)){
    			
    			onto_profil = onto_profil + " and in " + profel.getName();
    		}else{
    			
    			onto_profil = "The Ontology is in "+profel.getName();
    			
    		}
			
		}
    	
    	
    	if (profql.checkOntology(ontology).isInProfile()) {
    		
    		if(!onto_profil.equals(null)){
    			
    			onto_profil = onto_profil + " and in " + profql.getName();
    		}else{
    			
    			onto_profil = "The Ontology is in "+profql.getName();
    			
    		}
			
		}
    	
    	
    	if (profrl.checkOntology(ontology).isInProfile()) {
    		
    		if(!onto_profil.equals(null)){
    			
    			onto_profil = onto_profil + " and in " + profrl.getName();
    		}else{
    			
    			onto_profil = "The Ontology is in "+profrl.getName();
    			
    		}
			
		}
    	
    	
    	if(onto_profil.equals(null)){
    		
    		onto_profil = "unknown Profile (The Ontology is not in OWL 2 DL Profil)";
    	}
    	
    	return onto_profil;
		
	}
	
	
	
	public int getNumberofClasses(){
		
		if(ontology.getClassesInSignature().size()== 0){
			
			return 0;
		}else {
		
			return ontology.getClassesInSignature().size();
		}	
		
	}
	
	
	public int getNumberofDataProperties(){
		
		if (ontology.getDataPropertiesInSignature().size()==0) {
			
			return 0;
		} else {

			return ontology.getDataPropertiesInSignature().size();
		}
		
		
		
	}
	
	public int getNumberofObjectProperties(){
		
		if (ontology.getObjectPropertiesInSignature().size()==0) {
			
			return 0;
		} else {
			
			return ontology.getObjectPropertiesInSignature().size();
			
		}
		
	}
	
	public int getNumberofAnnotationsProperties(){
		
		if (ontology.getAnnotationPropertiesInSignature().size()==0) {
			
			return 0;
		} else {
			
			return ontology.getAnnotationPropertiesInSignature().size();

		}
		
	}
	
	public int getNumberofIndividuals(){
		
		if (ontology.getIndividualsInSignature().size()==0) {
			
			return 0;
		} else {
			
			return ontology.getIndividualsInSignature().size();

		}
		
		
	}
	
	public int getNumberofImports(){
		
		if (ontology.getImports().size()==0) {
			
			return 0;
		} else {
			
			return ontology.getImports().size();

		}
		
		
	}
	
	public Set<OWLOntology> getImports(){
		
		return ontology.getImports();
		
	}
	
//---------------------------------------------------------------------------------------------------------	
// List all owl classes without thing	
//---------------------------------------------------------------------------------------------------------
	//public List<String> list_owlclasses(){
	
	public Map<String,String> list_owlclasses(){	
		
		//List<String> owl_classes = new ArrayList<String>();
		Map<String,String> owl_classes = new HashMap<String, String>();
		
		
			for (OWLClass cls : ontology.getClassesInSignature()) {
			
				if(!cls.isOWLThing()){		
					
					//owl_classes.add(cls.getIRI().getFragment());
					
					String comment =""; 
					
					for (OWLAnnotation ann : cls.getAnnotations(ontology)) {
						
							comment+=ann.getValue().toString();
					}
					
					owl_classes.put(cls.getIRI().getFragment(), comment);

				
				}
					
			}
			
		return owl_classes;	
		
	}
	

//---------------------------------------------------------------------------------------------------------	
// List all name of DataProperties
//---------------------------------------------------------------------------------------------------------	
//	public List<String> list_dataproperties(){
	public Map<String,Map<List<String>,List<String>>> list_dataproperties(){	
		List<String> list_data = new ArrayList<String>();
		List<String> list_domain;
		List<String> list_range;
		
		//Dataproperty Bsp: currency   Domain: Money    Range : string
		
		Map<String,Map<List<String>,List<String>>> map_data = new HashMap<String,Map<List<String>,List<String>>>();
		Map<List<String>,List<String>> help_map;
		
		for (OWLDataProperty data : ontology.getDataPropertiesInSignature()) {
			
			list_domain = new ArrayList<String>();
			list_range = new ArrayList<String>();
			help_map = new HashMap<List<String>, List<String>>();
			
			
			list_data.add(data.asOWLDataProperty().getIRI().getFragment());
			
			for (OWLClassExpression dom : data.getDomains(ontology)) {
				
				if(!dom.isAnonymous()){
					
					list_domain.add(dom.asOWLClass().getIRI().getFragment());
				}
			}
			
			for(OWLDataRange range :data.getRanges(ontology)){
				
				list_range.add(range.asOWLDatatype().getIRI().getFragment());
				
			}
			
			
			help_map.put(list_domain, list_range);
			
			map_data.put(data.asOWLDataProperty().getIRI().getFragment(),help_map);
			
		}
		
		return map_data;
		
	}
	
//------------------------------------------------------------------------------------------------------------
// List all name of ObjectProperties
//------------------------------------------------------------------------------------------------------------

	public Map<String,Map<List<String>,List<String>>> list_objectproperties(){	
		List<String> list_data = new ArrayList<String>();
		List<String> list_domain;
		List<String> list_range;
		
		
		Map<String,Map<List<String>,List<String>>> map_data = new HashMap<String,Map<List<String>,List<String>>>();
		Map<List<String>,List<String>> help_map;
		
		for (OWLObjectProperty data : ontology.getObjectPropertiesInSignature()) {
			
			list_domain = new ArrayList<String>();
			list_range = new ArrayList<String>();
			help_map = new HashMap<List<String>, List<String>>();
			
			
			list_data.add(data.asOWLObjectProperty().getIRI().getFragment());
			
			for (OWLClassExpression dom : data.getDomains(ontology)) {
				
				if(!dom.isAnonymous()){
					
					list_domain.add(dom.asOWLClass().getIRI().getFragment());
				}
			}
			
			for(OWLClassExpression range :data.getRanges(ontology)){
				
				list_range.add(range.asOWLClass().getIRI().getFragment());
				
			}
			
	
			help_map.put(list_domain, list_range);
			
			map_data.put(data.asOWLObjectProperty().getIRI().getFragment(),help_map);
			
		}
		
		return map_data;
		
	}
	
	
//------------------------------------------------------------------------------------------------------------
//	Map from class and their subclasses
//------------------------------------------------------------------------------------------------------------	
		
	public Map<String,List<String>> sub_classes(){
		
		List<String>sub_owl_classes;
		
		Map<String,List<String>> sub_classes = new HashMap<String,List<String>>();
		
		for (OWLClass cls : ontology.getClassesInSignature()) {
			
			if(!cls.getSubClasses(ontology).isEmpty()){
				
				sub_owl_classes = new ArrayList<String>();
			
					for(OWLClassExpression exp : cls.getSubClasses(ontology)){
						
						for(OWLClass sub : exp.getClassesInSignature()){
							
							
								sub_owl_classes.add(sub.getIRI().getFragment());
						}
					}
					
					sub_classes.put(cls.getIRI().getFragment(), sub_owl_classes);
				
			}
			
		}
		
		return sub_classes;
		
	}
	
}
