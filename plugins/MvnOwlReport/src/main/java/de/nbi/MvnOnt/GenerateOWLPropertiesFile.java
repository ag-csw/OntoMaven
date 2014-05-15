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
package de.nbi.MvnOnt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class GenerateOWLPropertiesFile {

	
	protected void generate_property_file() {
	
		File file = new File("src/main/resources/");
		
		if(!file.exists()){
			file.mkdirs();
		}
		
		
		Properties properties = new Properties();	
		
		properties.setProperty("report.ontology.name", "Ontology Report");
		
		properties.setProperty("report.ontology.title", "The Documention of ");
		
		properties.setProperty("report.ontology.description","Description");
		
		properties.setProperty("report.ontology.format","Ontology Format is : ");
		
		properties.setProperty("report.ontology.profil", "Profil");
		
		
		
		properties.setProperty("report.ontology.import_title", "Imports : ");
		
		properties.setProperty("report.ontology.summary", "Summary");
		
		
		properties.setProperty("report.ontology.number_classes","Total Number of Classes");
		
		properties.setProperty("report.ontology.number_dataprop","Total Number of Datatype Properties");
		
		properties.setProperty("report.ontology.number_objectprop","Total Number of Object Properties");
		
		properties.setProperty("report.ontology.number_annotation","Total Number of Annotations Properties");
		
		properties.setProperty("report.ontology.number_inv","Total Number of Individuals");
		
		// ------------------------------------------------------------------------------------------------------
		
		properties.setProperty("report.onto.name", "Technical Report");
		
		properties.setProperty("report.onto.title", "A-Z of ");
		
		properties.setProperty("report.onto.description", "This is a complete alphabetical A-Z index of all terms, by class (categories or types) and by property.");
		
		properties.setProperty("report.onto.classes", "Classes: ");
		
		properties.setProperty("report.onto.properties", "Properties: ");
		
		properties.setProperty("report.onto.data_properties", "DataTypeProperties: ");
		
		properties.setProperty("report.onto.object_properties", "ObjectProperties: ");
		
		
		properties.setProperty("report.onto.status", "Status:");
		
		properties.setProperty("report.onto.has_subclasses", "Has Subclass:");
		
		properties.setProperty("report.onto.class_comment", "COMMENT:");
		
		properties.setProperty("report.onto.subclass_of", "Subclass Of:");
		
		properties.setProperty("report.onto.disjonit", "Disjoint With:");
		
		properties.setProperty("report.onto.domain", "Domain:");
		
		properties.setProperty("report.onto.range", "Range:");
		
	
		
		try {
			FileOutputStream out = new FileOutputStream("src/main/resources/owl-report.properties");
			
			properties.store(out, "Properties from OWL Report");
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}

}
