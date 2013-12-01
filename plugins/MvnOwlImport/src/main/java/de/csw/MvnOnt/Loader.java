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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyAlreadyExistsException;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyRenameException;

public class Loader {

	
	private String file_location;
	private boolean help_var;
	private File file;
	
	private List<IRI> iri = new ArrayList<IRI>();
	private List<String> message_list =new ArrayList<String>();
	private List<String>message_xmlcreator = new ArrayList<String>();
	private Map<IRI,String> elements_catalog = new HashMap<IRI, String>();
	
	private Map<OWLOntologyID,IRI> id_map = new HashMap<OWLOntologyID, IRI>();
	private Map<OWLOntologyID,IRI> id_map_file = new HashMap<OWLOntologyID, IRI>();
	
	private IRI iri_new;
	private List<IRI> save_list = new ArrayList<IRI>();
	private File [] file_array;
	
	private OWLOntologyManager manager;
	private OWLOntology ontology;
	private SaveOntology save;
	private XMLCatalogCreator creator;
	private Check_Exist_File exist;
    private IRI help_iri; 
    
   
	
	
	public Loader(List<IRI> iri,String file_location){
		
		this.file_location = file_location;
		this.iri =iri;
		
	}
	
	void loadTest(){
		
		file = new File(file_location);
		
	
		if(!file.exists() && !iri.isEmpty()){
			
			
			save = new SaveOntology(iri, file_location);
			
			save.save_Ontology();
			
			message_list = save.getMessageList();
			
			elements_catalog = save.getMap();
			
			
			
			creator = new XMLCatalogCreator(elements_catalog, file_location);
			
			creator.createXMLCatalog();
			
			message_xmlcreator = creator.getMessage();
			
			for (String messega : message_xmlcreator) {
				
				message_list.add(messega);
				
			}
			

		}
		
		else if(file.exists() && !iri.isEmpty()){
			
			
			for (IRI iri_var : iri) {
					
				manager = OWLManager.createOWLOntologyManager();
			
				if(iri_var.getScheme().equals("http")){
            		
	            	
            		exist = new Check_Exist_File(iri_var.toString());
            	
            		if(!exist.exist_File()){
            			String help_var;
            			
            			CheckXMLCatalog check = new CheckXMLCatalog(file_location,iri_var);
            			help_var=check.check();
            			
            			try{
            				if(!help_var.equals("null")){
            					
            					help_iri =IRI.create(help_var);
            					iri_var = help_iri;
            				}
            				}catch(NullPointerException e){
            				
            					message_list.add("the target is not in catalog.xml");
            				
            				}
            			
            		}
            	
            	}
				
				try {
						
					try{
						
						ontology = manager.loadOntologyFromOntologyDocument(iri_var);
	
						id_map.put(ontology.getOntologyID(), iri_var);
						
						manager.removeOntology(ontology);
						
					}catch(OWLOntologyAlreadyExistsException e){
						
						message_list.add("the ontology already exist");
						
					}catch (OWLOntologyRenameException e) {
						
						message_list.add("it was not possible to rename the ontology");
						
					}
					
					
				} catch (OWLOntologyCreationException e) {
					
					message_list.add("The Ontology could not be created, see to the class"+getClass().getName());
					
				}
				
			}
		
			
			file_array = file.listFiles();
	
			
			for (File fl : file_array) {
				
				manager = OWLManager.createOWLOntologyManager();
				
				try {
					
					if(fl.getName().endsWith(".owl")){
					
						try{
							
							ontology = manager.loadOntologyFromOntologyDocument(fl.getAbsoluteFile());
						
							iri_new = IRI.create(fl.getAbsoluteFile());
						
							id_map_file.put(ontology.getOntologyID(), iri_new);
							
							manager.removeOntology(ontology);
							
						}catch(OWLOntologyAlreadyExistsException e){
						
							message_list.add("the ontology already exist, see to the class "+getClass().getName());
							
						}catch(OWLOntologyRenameException e){
							
							message_list.add("it was not possible to rename the ontology");
						}
						
					}
					
					
				} catch (OWLOntologyCreationException e) {
					
					message_list.add("The ontology could not be created, see the class "+getClass().getName());
					
				} 
				
			}
			
			if(!id_map.isEmpty() && id_map_file.isEmpty()){
				
				for (Map.Entry<OWLOntologyID,IRI> i_map : id_map.entrySet()) {
					
					save_list.add(i_map.getValue());
					
				}
				
				save = new SaveOntology(save_list, file_location);
				
				save.save_Ontology();
				
				message_list = save.getMessageList();
				
				elements_catalog = save.getMap();
				
				creator = new XMLCatalogCreator(elements_catalog, file_location);
				
				
				if(creator.existXMLCatalog()){
					

					
					creator.updateXMLCatalog();
					
					message_xmlcreator = creator.getMessage();
					
					if(!message_xmlcreator.isEmpty()){
						
					
						for (String messega : message_xmlcreator) {
						
							message_list.add(messega);
						
						}
					}	
					
					
				}else{
					
					creator.createXMLCatalog();
					
					message_xmlcreator = creator.getMessage();
					
					if(!message_xmlcreator.isEmpty()){
						
					
						for (String messega : message_xmlcreator) {
						
							message_list.add(messega);
						
						}
					}	
					
				}
				
				
				
			}
			
			if(!id_map.isEmpty() && !id_map_file.isEmpty()){
				
				
				for (Map.Entry<OWLOntologyID, IRI> i : id_map.entrySet()) {
					
					help_var=true;
					
					for (Map.Entry<OWLOntologyID, IRI> k : id_map_file.entrySet()) {
						
						if(i.getKey().equals(k.getKey())){
							help_var=false;
							break;
						}
					}
					
					if(help_var){
					
						save_list.add(i.getValue());
						
					}
					
				}
				
				if(!save_list.isEmpty()){
					
					save = new SaveOntology(save_list, file_location);
					
					save.save_Ontology();
					
					message_list = save.getMessageList();
					
					elements_catalog = save.getMap();
					
					creator = new XMLCatalogCreator(elements_catalog, file_location);
					
					
					if(creator.existXMLCatalog()){
							
		
						creator.updateXMLCatalog();
						
						message_xmlcreator = creator.getMessage();
						
						if(!message_xmlcreator.isEmpty()){
							
						
							for (String messega : message_xmlcreator) {
							
								message_list.add(messega);
							
							}
						}	
						
						
					}else{
						
						creator.createXMLCatalog();
						
						message_xmlcreator = creator.getMessage();
						
						if(!message_xmlcreator.isEmpty()){
							
						
							for (String messega : message_xmlcreator) {
							
								message_list.add(messega);
							
							}
						}	
						
					}
					
				}
				
			}
			
			
		}
		
		
	}
	
	
	
	List<String> getMessage(){
		
		return message_list;
	}

}
