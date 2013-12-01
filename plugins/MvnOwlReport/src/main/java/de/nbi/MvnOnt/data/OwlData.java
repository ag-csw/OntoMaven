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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class OwlData {

//	private String location;
//	private File file;
	
	private OWLOntology ontology;
	private OWLOntologyManager manager; 
	
	private List sub_list =new ArrayList();
	private Map map = new HashMap();
	private List all_list = new ArrayList();
	
	
	private List<String> owl_classes;
	private Map<String,List<String>> sub_classes;
	
	
	public OwlData(String owl_file){
		
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
	
	

	
// List all owl classes without thing	
	public List<String> list_owlclasses(){
		
		owl_classes = new ArrayList<String>();
		
		for (OWLClass cls : ontology.getClassesInSignature()) {
			
				if(!cls.isOWLThing()){
					
					owl_classes.add(cls.getIRI().getFragment());
					
				
					}
			}
			
		return owl_classes;	
		
	}
	
	
//	Map from class and their subclasses
	public Map<String,Map<String,String>> sub_cls(){
		
		Map<String, String> map_map ;
		
		Map<String,Map<String,String>> sb_classes = new HashMap<String,Map<String,String>>();
		
		for (OWLClass cls : ontology.getClassesInSignature()) {
			
			if(!cls.getSubClasses(ontology).isEmpty()){
				
				map_map = new HashMap<String, String>();
			
					for(OWLClassExpression exp : cls.getSubClasses(ontology)){
						
						for(OWLClass sub : exp.getClassesInSignature()){
							
								map_map.put(sub.getIRI().getFragment(), "suit");
								
						}
					}
					
					sb_classes.put(cls.getIRI().getFragment(), map_map);
					
				
			}
			
		}
		
		return sb_classes;
		
	}
	
	
	//protected Map<String,Map<List<String>,List<String>>> getDataProperty(){
	public Map<String,String> getDataProperty(){	
		
		Map<String,String> help_map ;
		List<String> list_one  = new ArrayList<String>();;
		Map<String,String> dat_rang = new HashMap<String, String>();
		
		String dom = "";
		String range = "";
		
		
		//Dataproperty Bsp: currency   Domain: Money    Range : string
		
			for (OWLDataProperty data : ontology.getDataPropertiesInSignature()) {
			
				
				//list_two = new ArrayList<String>();
				help_map = new HashMap<String,String>();
				
				//<<<<<<<<<<<<<<<property>>>>>>>>>>>>>>>>>>>>
				data.asOWLDataProperty().getIRI().getFragment();
							
				//<<<<<<<<<<<<<<<Domain>>>>>>>>>>>>>>>>>>>>>>>
				for(OWLClassExpression clsexp :  data.getDomains(ontology)){
					
					
					if(!clsexp.isAnonymous()){
						
						dom = clsexp.asOWLClass().getIRI().getFragment();
		
					}
					
				}
				
				
				
				//<<<<<<<<<<<<<<<<<<Range>>>>>>>>>>>>>>>>>>>>
				for (OWLDataRange dr : data.getRanges(ontology)) {

					if(dr.isDatatype()){
					

						range = dr.asOWLDatatype().getIRI().getFragment();
					}
				}
				
				
				
				list_one.add(dom+data.asOWLDataProperty().getIRI().getFragment()+range);
			
				
				help_map.put(dom,data.asOWLDataProperty().getIRI().getFragment()+":"+range+"<br>");
				boolean bool = false;
				
				if(!help_map.isEmpty()){
					
					for (Map.Entry st : help_map.entrySet()) {
						
						if(dat_rang.isEmpty()){
							
							dat_rang.put(st.getKey().toString(),st.getValue().toString());
						}else{
							
							for (Map.Entry ent : dat_rang.entrySet()) {
								
								if(st.getKey().equals(ent.getKey())){
									
									String tt = (String)ent.getValue()+st.getValue();
									
									ent.setValue(tt);
									
									bool = false;
									break;
								}else{
									
									bool = true;
								}
							}
							
							if(bool){
							
								dat_rang.put(st.getKey().toString(),st.getValue().toString());
							}
						}
						
					}
				
				}
			
			}
		
		return dat_rang;
		
	}
	
	
//------------------------------------------------------------------------------------------	
	
	
	
	
	
	
	
	public Map<String,String> getObjectProperty(){	
		
		Map<String,String> help_map ;
		List<String> list_one  = new ArrayList<String>();;
		Map<String,String> dat_rang = new HashMap<String, String>();
		
		String dom = "";
		String range = "";
		
		
		//Dataproperty Bsp: currency   Domain: Money    Range : string
		
			for (OWLObjectProperty object : ontology.getObjectPropertiesInSignature()) {
			
				
				//list_two = new ArrayList<String>();
				help_map = new HashMap<String,String>();
				
				//<<<<<<<<<<<<<<<property>>>>>>>>>>>>>>>>>>>>
				object.asOWLObjectProperty().getIRI().getFragment();
							
				//<<<<<<<<<<<<<<<Domain>>>>>>>>>>>>>>>>>>>>>>>
				for(OWLClassExpression clsexp :  object.getDomains(ontology)){
					
					
					if(!clsexp.isAnonymous()){
						
						dom = clsexp.asOWLClass().getIRI().getFragment();
		
					}
					
				}
				
				
				
				//<<<<<<<<<<<<<<<<<<Range>>>>>>>>>>>>>>>>>>>>
				for (OWLClassExpression dr : object.getRanges(ontology)) {

					if(!dr.isAnonymous()){
					

						range = dr.asOWLClass().getIRI().getFragment();
					}
				}
				
				
				
				list_one.add(dom+object.asOWLObjectProperty().getIRI().getFragment()+range);
			
				
				help_map.put(dom,object.asOWLObjectProperty().getIRI().getFragment()+":"+range+"<br>");
				boolean bool = false;
				
				if(!help_map.isEmpty()){
					
					for (Map.Entry st : help_map.entrySet()) {
						
						if(dat_rang.isEmpty()){
							
							dat_rang.put(st.getKey().toString(),st.getValue().toString());
						}else{
							
							for (Map.Entry ent : dat_rang.entrySet()) {
								
								if(st.getKey().equals(ent.getKey())){
									
									String tt = (String)ent.getValue()+st.getValue();
									
									ent.setValue(tt);
									
									bool = false;
									break;
								}else{
									
									bool = true;
								}
							}
							
							if(bool){
							
								dat_rang.put(st.getKey().toString(),st.getValue().toString());
							}
						}
						
					}
				
				}
			
			}
		
		return dat_rang;
		
	}

}
