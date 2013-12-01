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


import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;


public class SaveOntology
{

    private List<String> message_list = new ArrayList<String>();
    private List<IRI> list_iri;
    private String file_location;
    private OWLOntology ontology;
    private OWLOntologyManager manager;
    private FileOutputStream out;
    private String file_name;
    private String string_array[];
    private File file;
    private Map<IRI,String> map;

    public SaveOntology(List<IRI> list_iri, String file_location)
    {
       
        map = new HashMap<IRI, String>();
        
        this.list_iri = list_iri;
        this.file_location = file_location;
    }

    void save_Ontology()
    {
        
        
        if(!list_iri.isEmpty()){
           
        	
        	
        	for(IRI iri:list_iri){
        		
        		
        		
        		manager = OWLManager.createOWLOntologyManager();
        		file_name = iri.getFragment();
        		
        		string_array = file_name.split(Pattern.quote("."));
        		
        	
        		
        		if(!string_array[string_array.length - 1].equals("owl")){
        			
        			file_name = file_name + ".owl";
        		}

        		try{
        			
        			file = new File(file_location);
        			
        			if(!file.exists()){
        				
        				file.mkdir();
        				
        			
        			}
        				
        				file = new File(file_location+"/"+file_name);
        			
        				ontology = manager.loadOntologyFromOntologyDocument(iri);
        			
        				out = new FileOutputStream(file);
        		
        				if(!ontology.isEmpty() && manager.getOntologyFormat(ontology).isPrefixOWLOntologyFormat())
        				{
        					manager.saveOntology(ontology, out);
        		
        					map.put(iri, file_name);
        		
        					message_list.add("Ontology -----> "+ontology.getOntologyID().getOntologyIRI().getFragment()+"  <----- saved in----> "+file.getAbsolutePath());
        					
        				}else{
        				
        					message_list.add("Ontology iy Empty or Ontology has the false format");
        					
        				}
        			
        					out.close();
        					manager.removeOntology(ontology);
        					
        				
        		}
        		catch(OWLOntologyCreationException e)
        		{
        			
        			message_list.add("Ontology not loaded");
        		}
        		catch(OWLOntologyStorageException e)
        		{
        		
        			message_list.add("Ontology could not be written");
        		}
        		catch(FileNotFoundException e)
        		{
        			
        			message_list.add("File not found");
        		}
        		catch(NullPointerException e)
        		{
        			message_list.add("False Format");
        			
        		} catch (IOException e) {
				
        			message_list.add("File could not be closed");
					
				}
        		
        	}
        
        }
    }
    
    public Map<IRI,String> getMap(){
    	return map;
    }
    
    public List<String> getMessageList(){
    	
    	return message_list;
    }
}
