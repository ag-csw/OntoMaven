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
import java.net.URI;
import java.util.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.*;
import org.semanticweb.owlapi.model.*;

import uk.ac.manchester.cs.owl.owlapi.ParsableOWLOntologyFactory;

public class Importer
{

    private URI iri;
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private Set<OWLImportsDeclaration> set;
    private Set<OWLImportsDeclaration> new_set;
    private File file;
 
    private ParsableOWLOntologyFactory parsa_factory;
    private IRIDocumentSource source;
    private List<IRI> import_list;
    private Set<IRI> set_iri;
    private List<String> miss_import_messeage;
    private List<String> succes_import_message;
   
    private Check_Exist_File exist;
    private IRI iri_iri;
    private IRI help_iri; 
    private String location;
    

    public Importer(URI iri,String location)
    {
        set_iri = new HashSet<IRI>();
        miss_import_messeage = new ArrayList<String>();
        succes_import_message = new ArrayList<String>();
        
        this.iri = iri;
        this.location=location;
    }

    List<IRI> all_Import() throws OWLOntologyCreationException{
	
		
    	
        import_list = new ArrayList<IRI>();
        set = new HashSet<OWLImportsDeclaration>();
        IRI iri_new = IRI.create(iri);
        manager = OWLManager.createOWLOntologyManager();
        try
        {
            ontology = manager.loadOntologyFromOntologyDocument(iri_new);
            set = ontology.getImportsDeclarations();
        }
        catch(StackOverflowError e)
        {
            miss_import_messeage.add("cyclic import");
        }
        catch(OWLOntologyAlreadyExistsException e)
        {
            miss_import_messeage.add(e.getLocalizedMessage());
        }
        
        while (!set.isEmpty()) {

			set = transitive_Import(set);
		

		}
        
        
        if(!set_iri.isEmpty()){
			
			for (IRI iri_to_list : set_iri) {

				import_list.add(iri_to_list);

			}
		}

        
        return import_list;
    }

    Set<OWLImportsDeclaration> transitive_Import(Set<OWLImportsDeclaration> set)
        throws OWLOntologyCreationException
    {
        new_set = new HashSet<OWLImportsDeclaration>();
       
        for (OWLImportsDeclaration owlImportsDeclaration : set) {
        
           
            manager = OWLManager.createOWLOntologyManager();
            String url = owlImportsDeclaration.getURI().getPath();
            file = new File(url);
            if(!file.exists() && owlImportsDeclaration.getURI().getScheme().equals("file"))
            {
            	
                miss_import_messeage.add("File could not be found --> "+owlImportsDeclaration.getIRI().toString());
               
            } else
            {
            
            	iri_iri = owlImportsDeclaration.getIRI();
           	 	
            	if(owlImportsDeclaration.getIRI().getScheme().equals("http")){
            		
            	
            		exist = new Check_Exist_File(owlImportsDeclaration.getIRI().toString());
            	
            		if(!exist.exist_File()){
            			String help_var;
               	 
            			CheckXMLCatalog check = new CheckXMLCatalog(location,owlImportsDeclaration.getIRI());
            			help_var=check.check();
            			
            			try{
            				if(!help_var.equals("null")){
            					
            					help_iri =IRI.create(help_var);
            					iri_iri = help_iri;
            				}
            				}catch(NullPointerException e){
            				
            					miss_import_messeage.add("the target is not in xmlcatalog----->"+iri_iri.toString());
            					
            				}
            			
            		}
            	
            	}
            			
                try
                {
                    parsa_factory = new ParsableOWLOntologyFactory();
                    source = new IRIDocumentSource(owlImportsDeclaration.getIRI());
                    if(parsa_factory.canLoad(source))
                    {
                    	
                       // ontology = manager.loadOntologyFromOntologyDocument(owlImportsDeclaration.getIRI());
                    	
                    	ontology = manager.loadOntologyFromOntologyDocument(iri_iri);
                    	
                        if(manager.getOntologyFormat(ontology).isPrefixOWLOntologyFormat())
                        {
                            set_iri.add(owlImportsDeclaration.getIRI());
                            
                            succes_import_message.add("Import ontology successful  --> "+owlImportsDeclaration.getIRI().toString());
                        } else
                        {
                            miss_import_messeage.add("False Format or Ontology could not be found --> "+ owlImportsDeclaration.getIRI().toString());
                        }
                        if(!ontology.getImports().isEmpty())
                        {
                            new_set = ontology.getImportsDeclarations();
                        }
                        manager.removeOntology(ontology);
                    } else
                    {
                        miss_import_messeage.add("Target file doesn't correspond any Ontology Format -->");
                    }
                }
                catch(OWLOntologyCreationIOException e)
                
                {
                	 
                	 
                    miss_import_messeage.add("File could not be found --> "+ e.getMessage().toString());
                    
                }
                catch(UnparsableOntologyException e)
                {
                    miss_import_messeage.add("Could not parse this target --> "+owlImportsDeclaration.getIRI()+" <-- Perhaps the target is no ontology");
                }
                catch(OWLOntologyAlreadyExistsException e)
                {
                    miss_import_messeage.add(e.getLocalizedMessage());
                }
            }
        }

        return new_set;
    }

    public List<String> getMiss_import_messeage()
    {
        return miss_import_messeage;
    }

    public List<String> getSucces_import_messeage()
    {
        return succes_import_message;
    }
}

