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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.semanticweb.owlapi.model.IRI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

public class XMLCatalogCreator
{

    private Map<IRI,String> map;
    private String file_location;
    private File file;
    private List<String> list_message = new ArrayList<String>();
    private Document document;
    private LSSerializer serializer;
    private DOMImplementationRegistry registry;
    private DOMImplementationLS ls;
    private LSOutput output;
    private FileOutputStream out;
    private final String file_name = "catalog.xml";
    
    private Element root;
    private Element public_elem;
    
    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;

    public XMLCatalogCreator(Map<IRI, String> map, String file_location)
    {
        this.map = new HashMap<IRI, String>();
        this.map = map;
        this.file_location = file_location;
    }

    void createXMLCatalog()
    {
        if(!map.isEmpty())
        {

        	
            factory = DocumentBuilderFactory.newInstance();
            try
            {
                builder = factory.newDocumentBuilder();
                document = builder.newDocument();
            }
            catch(ParserConfigurationException e)
            {
                
                list_message.add("Error while trying to instatiate Documentbuilder");
            }
           
            root = document.createElement("catalog");
            
            root.setAttribute("xmlns", "urn:oasis:names:tc:entity:xmlns:xml:catalog");
            document.appendChild(root);
            
 
            for ( Map.Entry<IRI, String> entry : map.entrySet()) {
				
            	
            	public_elem = document.createElement("system");
                public_elem.setAttribute("systemId", entry.getKey().toString());
                public_elem.setAttribute("uri", entry.getValue().toString());
                
                root.appendChild(public_elem);
            }

            try
            {
            	
           
            	
            	file = new File(file_location);
            	
            	if (!file.exists()){
            		
            		file.mkdir();
            	}
          
            	out = new FileOutputStream(file+"\\"+file_name);
                
                registry = DOMImplementationRegistry.newInstance();
                ls = (DOMImplementationLS)registry.getDOMImplementation("LS");
                serializer = ls.createLSSerializer();
                output = ls.createLSOutput();
                output.setByteStream(out);
                
                serializer.write(document, output);
               
                list_message.add("The File catalog.xml would be created in  file location:  "+ file.getPath()+"\\"+file_name);
                
                out.close();
            }
            catch(ClassNotFoundException e)
            {
                list_message.add("Error catalog.xml could not be written : ClassNotFoundException");
            }
            catch(InstantiationException e)
            {
                list_message.add("Error catalog.xml could not be written : InstantiationException");
            }
            catch(IllegalAccessException e)
            {
                list_message.add("Error catalog.xml could not be written : IllegalAccesException");
            }
            catch(ClassCastException e)
            {
                list_message.add("Error catalog.xml could not be written : ClassCastException");
            }
            catch(FileNotFoundException e)
            {
                list_message.add("Error catalog.xml could not be written : FileNotFoundException");
            }
            catch(IOException e)
            {
                list_message.add("Error catalog.xml could not be written : IOException");
            }
        }
    }
    
    void updateXMLCatalog(){
    	
    	file = new File(file_location+"\\"+file_name);
    	
    	Map<IRI,String> copy_map = new HashMap<IRI,String>();
    	NodeList nodelist;
    	
    	Element element;
    	
    	try {
			
			factory = DocumentBuilderFactory.newInstance();
			
			builder = factory.newDocumentBuilder();
			
			document = builder.parse(file);
			
			root = (Element)document.getFirstChild();
			
			
			// �berpr�fen ob schon elemente in catalog.xml existieren			
			
			boolean test = true;
			
			
			nodelist = root.getChildNodes();
		
			for ( Map.Entry<IRI, String> entry : map.entrySet()) {
				
				for (int i = 0; i < nodelist.getLength(); i++) {
					
					element=(Element) nodelist.item(i);
					
					if(element.getAttribute("systemId").toString().equals(entry.getKey().toString())){
						
						test = false;
						break;
						
					}
					
					
				}
				
				if(test){
					
					copy_map.put(entry.getKey(), entry.getValue());
					
				}
				
			}
			
			
			
			// Die Elemente die nicht in Catalog.xml existieren, die zuf�gen
			
			nodelist = root.getChildNodes();
			
			
			for ( Map.Entry<IRI, String> entry : copy_map.entrySet()) {
				
            	public_elem = document.createElement("system");
                public_elem.setAttribute("systemId", entry.getKey().toString());
                public_elem.setAttribute("uri", entry.getValue().toString());
                
               
               // root.getLastChild().appendChild(public_elem);
                
                root.insertBefore(public_elem, root.getLastChild());
                
            }
			
			out = new FileOutputStream(file);
			
			
            
            registry = DOMImplementationRegistry.newInstance();
            ls = (DOMImplementationLS)registry.getDOMImplementation("LS");
            serializer = ls.createLSSerializer();
            output = ls.createLSOutput();
            output.setByteStream(out);
            
           
            serializer.write(document, output);
            
            list_message.add("The File catalog.xml would be updated in  file location:  "+ file.getParent());
            
            out.close();

    	} catch (FileNotFoundException e) {
			
    		list_message.add("File could not be found, see to the class"+getClass().getName());
			
		} catch (ParserConfigurationException e) {
			
			list_message.add("False Configuration for Parser, see to the class"+getClass().getName());
			
		} catch (SAXException e) {
			
			list_message.add("Could not parse it, see to the class"+getClass().getName());
			
		} catch (IOException e) {
			
			list_message.add("IOException see to the class"+getClass().getName());
			
		} catch (ClassNotFoundException e) {
			
			list_message.add("ClassNotFoundException see to the class"+getClass().getName());
			
		} catch (InstantiationException e) {
			
			list_message.add("InstantiationException see to the class"+getClass().getName());
			
		} catch (IllegalAccessException e) {
			
			list_message.add("IllegalAccesException see to the class"+getClass().getName());
			
		} catch (ClassCastException e) {
			
			list_message.add("ClassCastException see to the class"+getClass().getName());
			
		}
    	
    }
    
    boolean existXMLCatalog(){
    	
    	boolean catalog_exist;
    	
    	file = new File(file_location+"//"+file_name);
    	
    	if(file.exists()){
    		
    		catalog_exist = true;
    		
    	}else {
			
    		catalog_exist = false;
		}
    	
    	return catalog_exist;
    	
    }
    
    public List<String> getMessage(){
    	
    	return list_message;
    	
    }
}
