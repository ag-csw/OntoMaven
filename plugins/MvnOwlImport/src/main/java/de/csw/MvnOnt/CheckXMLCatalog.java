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

import org.apache.xerces.parsers.DOMParser;
import org.apache.xml.resolver.CatalogManager;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.semanticweb.owlapi.model.IRI;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class CheckXMLCatalog
{

    private String getResolved = null;
    private IRI systemid;
    private String catalog_location;

    public CheckXMLCatalog(String catalog_location, IRI systemid)
    {
        this.systemid=systemid;
        this.catalog_location = catalog_location;
       
    }

    String check(){
    	
    	DOMParser parser = new DOMParser();
    	
    	catalog_location = catalog_location +"catalog.xml";
    	
    	try {
			parser.setFeature("http://xml.org/sax/features/validation", true);
			
			CatalogManager manag = new CatalogManager();
			manag.setAllowOasisXMLCatalogPI(true);
			manag.setCatalogClassName("org.apache.xml.resolver.Resolver");
			manag.setCatalogFiles(catalog_location);
			manag.setIgnoreMissingProperties(false);
			manag.setPreferPublic(true);
			manag.setRelativeCatalogs(true);
			manag.setUseStaticCatalog(true);
			manag.setVerbosity(99);
			
			CatalogResolver res = new CatalogResolver(manag);
		
			parser.setEntityResolver(res);
			
			getResolved = res.getResolvedEntity(null, systemid.toString());
			
			
		} catch (SAXNotRecognizedException e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return getResolved;
    	
    }
}
    
    
