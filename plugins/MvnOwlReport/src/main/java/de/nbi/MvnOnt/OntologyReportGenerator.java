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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.codehaus.doxia.sink.Sink;
import org.semanticweb.owlapi.model.OWLOntology;

import de.nbi.MvnOnt.data.PrepareOwlData;



public class OntologyReportGenerator {
	

	  /**
	   * Bundle, um die fixen Texte zuzusteuern.
	   */
	  private ResourceBundle bundle;

	  /**
	   * Hier werden die Dokument-Ereignise ausgel�st.
	   */
	  private Sink sink;
	  
	  //private PrepareOwlData prepare;
	  
	  private String owl_file;
	  
	  private PrepareOwlData prepare = new PrepareOwlData(owl_file);
	  
	  
	  public OntologyReportGenerator(Sink sink, ResourceBundle bundle, String owl_file){
		  
		  this.sink = sink;
		  
		  this.bundle = bundle;
		  
		  this.owl_file=owl_file;
		  
		  
		  
	  }
	  
	  //----------------------------------------------------------------------------
	  
	  private String getTitle(){
		  
		  return bundle.getString("report.ontology.title")+prepare.getOntID();
	  }
	  
	  //-------------------------------------------------------------------------------
	  
	  public void generateReport(){
		  
		  doHeading();
		  doImporting();
		  doSummary();
		  
		  sink.body_();
		  sink.flush();
		  
	  }
	  
	  //-----------------------------------------------------------------------------------
	  
	  private void doHeading()
	  {
	    sink.head();
	    sink.title();
	    sink.text(getTitle());
	    sink.title_();
	    sink.head_();

	    sink.body();

	    sink.section1();
	    sink.sectionTitle1();
	    sink.text(getTitle());
	    sink.sectionTitle1_();

	    sink.paragraph();
	    sink.text(bundle.getString("report.ontology.description")+" "+prepare.getComment());
	    sink.paragraph_();
	    
	    sink.paragraph();
	    sink.text(bundle.getString("report.ontology.format")+" "+prepare.getOntologyFormat());
	    sink.paragraph_();
	    sink.paragraph();
	    sink.text(bundle.getString("report.ontology.profil")+" "+ prepare.getProfile());
	    sink.paragraph_();
	    
	    sink.section1_();
	   
	    
	  }

	  private void doImporting(){
		  
		  	sink.table();
		  
		    sink.tableRow();
		    sink.tableHeaderCell();
		    sink.text(bundle.getString("report.ontology.import_title"));
		    sink.tableHeaderCell_();
		    sink.tableRow_();
		    
		    if(prepare.getNumberofImports() > 0){
		    
		    	for ( OWLOntology imp : prepare.getImports()) {
		    		
			    	sink.tableRow();
				    sink.tableCell();
				    sink.text(imp.toString());
				    sink.tableCell_();
				    sink.tableRow_();
					
				}
		    
		    
		    }else{
		    	
		    	sink.tableRow();
			    sink.tableCell();
			    sink.text("found no import");
			    sink.tableHeaderCell_();
			    sink.tableCell_();
		    }
		    
		    sink.table_();
		    
		    sink.section1_();
		
	  }
	  
      //----------------------------------------------------------------------------------------
	  private void doSummary(){
		  
		  sink.section2();
		  sink.title();
		  sink.text(bundle.getString("report.ontology.summary"));
		  sink.title_();
		  
		  sink.table();
		  
		  sink.tableRow();
		  sink.tableHeaderCell();
		  sink.text("");
		  sink.tableHeaderCell_();
		  sink.tableHeaderCell();
		  sink.text("number");
		  sink.tableHeaderCell_();
		  sink.tableRow_();
		  
		  sink.tableRow();
		  sink.tableCell();
		  sink.text(bundle.getString("report.ontology.number_classes"));
		  sink.tableCell_();
		  sink.tableCell();
		  sink.text(Integer.toString(prepare.getNumberofClasses()));
		  sink.tableCell_();
		  sink.tableRow_();
		  
		  sink.tableRow();
		  sink.tableCell();
		  sink.text(bundle.getString("report.ontology.number_dataprop"));
		  sink.tableCell_();
		  sink.tableCell();
		  sink.text(Integer.toString(prepare.getNumberofDataProperties()));
		  sink.tableCell_();
		  sink.tableRow_();
		  
		  sink.tableRow();
		  sink.tableCell();
		  sink.text(bundle.getString("report.ontology.number_objectprop"));
		  sink.tableCell_();
		  sink.tableCell();
		  sink.text(Integer.toString(prepare.getNumberofObjectProperties()));
		  sink.tableCell_();
		  sink.tableRow_();
		  
		  sink.tableRow();
		  sink.tableCell();
		  sink.text(bundle.getString("report.ontology.number_annotation"));
		  sink.tableCell_();
		  sink.tableCell();
		  sink.text(Integer.toString(prepare.getNumberofAnnotationsProperties()));
		  sink.tableCell_();
		  sink.tableRow_();
		  
		  sink.tableRow();
		  sink.tableCell();
		  sink.text(bundle.getString("report.ontology.number_inv"));
		  sink.tableCell_();
		  sink.tableCell();
		  sink.text(Integer.toString(prepare.getNumberofIndividuals()));
		  sink.tableCell_();
		  sink.tableRow_();
		  
		  sink.table_();
		  
		  sink.section2_();
		  
		  
		  
		  
	  }
	  // -------------------------------------------------------------------------------------
	  
	  //-------------------------------------------------------------------------------
	  
	  public void generateTechReport(){
		  
		  doTechHeading();
		  doBody();
		  doDetail();
		  
		  sink.body_();
		  sink.flush();
		  
	  }
	  
	  //-----------------------------------------------------------------------------------
	  
	  private void doTechHeading(){
		  
		  sink.head();
		  sink.title();
		  sink.text("Details about the ontology");
		  sink.title_();
		  sink.head_();
		  
		  sink.body();
		  
		  sink.section1();
		  sink.sectionTitle1();
		  sink.text(bundle.getString("report.onto.title"));
		  sink.sectionTitle1_();
		  
		  sink.paragraph();
		  sink.text(bundle.getString("report.onto.description"));
		  sink.paragraph_();
		  
		  sink.section1_();
	  }
	  
	  //-----------------------------------------------------------------------------------
	  
 private void doBody(){
		  
		  sink.section2();
	
		  sink.table();
		  
		  sink.tableRow();
		  sink.tableHeaderCell();
		  sink.text(bundle.getString("report.onto.classes"));
		  sink.tableHeaderCell_();
		  sink.tableRow_();
		  
		  sink.tableRow();
		  sink.tableCell();
		  
		 // for (String cls : prepare.list_owlclasses()) {
		  for (Map.Entry cls : prepare.list_owlclasses().entrySet()) {
			  
			  sink.link("#"+cls.getKey());
			  sink.text(cls.getKey() +"|");
			 sink.link_();
			
			}
		  //sink.text("test test test");
		  
		  sink.tableCell_();
		  sink.tableRow_();
		  
		  sink.table_();
		  
		  sink.table();
		  
		  sink.tableRow();
		  sink.tableHeaderCell();
		  sink.text(bundle.getString("report.onto.properties"));
		  sink.tableHeaderCell_();
		  sink.tableRow_();
		  
		  sink.tableRow();
		  sink.tableCell();
		  
		  
		  
		 // for (String obj : prepare.list_objectproperties()) {
	     for(Map.Entry object : prepare.list_objectproperties().entrySet()){ 	  
			  
			  sink.link("#"+object.getKey());
			  sink.text(object.getKey() +"|");
			  sink.link_();
			
			}
		   
		   //for (String data : prepare.list_dataproperties()) {
			   
			for(Map.Entry data : prepare.list_dataproperties().entrySet()){   
			  
			  //sink.link("#"+data);
			  //sink.text(data +"|");
			  sink.link("#"+data.getKey());
			  sink.text(data.getKey() +"|");
			  sink.link_();
			
			}
		
			
		  sink.tableCell_();
		  sink.tableRow_();
		  
		  
		  
		  sink.table_();
		  sink.section2_();
		  
	  }
	  
	private void doDetail(){
	
		 List<String> sb_cl = new ArrayList<String>();
		 Map<List<String>,List<String>> help_map = new HashMap<List<String>, List<String>>();
		  
		  sink.section2();
		  sink.sectionTitle2();
		  sink.text("More Details");
		  sink.sectionTitle2_();
		  
		  //for (String cls : prepare.list_owlclasses()) {
		  for (Map.Entry cls : prepare.list_owlclasses().entrySet()) {
			  
			  sink.table();
			  
			  sink.tableRow();
			  sink.tableHeaderCell();
			  sink.text(cls.getKey().toString());
			  sink.tableHeaderCell_();
			  sink.tableRow_();
			  
			  sink.rawText("<tr id="+cls.getKey()+">");
			  sink.rawText("<td>");
			  
			  sink.list();
			  sink.listItem();
			  sink.text(bundle.getString("report.onto.has_subclasses"));
			  
			for (Map.Entry entry : prepare.sub_classes().entrySet()) {
				  
				  if(cls.getKey().equals(entry.getKey())){
					  
					  //sb_cl = (List)entry.getValue();
					  
					  for (String st : (List<String>)entry.getValue()) {
						  
						  sink.link("#"+st);
						  sink.text(" "+st+"| ");
						  sink.link_();
						
					}
					  
				  }
				  
				
			}
			  
			  sink.listItem_();
			  sink.list_();
			  
//---------------------------------------------------------------------------------------------
			  
			  sink.list();
			  sink.listItem();
			  sink.text(bundle.getString("report.onto.data_properties"));
			  
			for (Map.Entry entry : prepare.list_dataproperties().entrySet()) {
				
				
				
				 for (Map.Entry data : ((Map<List<String>, List<String>>) entry.getValue()).entrySet()){
					 
					 
					 for(String dom : (List<String>)data.getKey()){
						 
						 if(cls.getKey().equals(dom)){
							 
							 sink.link("#"+entry.getKey());
							  sink.text(" "+entry.getKey()+"| ");
							  sink.link_();
							 
						 }
						 
					 }
					 
					
				}
				  
				
			}
			  
			  sink.listItem_();
			  sink.list_();
			  
//----------------------------------------------------------------------------------------------
			  
			  sink.list();
			  sink.listItem();
			  sink.text(bundle.getString("report.onto.object_properties"));
			  
			for (Map.Entry entry : prepare.list_objectproperties().entrySet()) {
				
				
				 for (Map.Entry data : ((Map<List<String>, List<String>>) entry.getValue()).entrySet()){
					 
					 
					 for(String dom : (List<String>)data.getKey()){
						 
						 if(cls.getKey().equals(dom)){
							 
							 sink.link("#"+entry.getKey());
							  sink.text(" "+entry.getKey()+"| ");
							  sink.link_();
							 
						 }
						 
					 }
					 
					
				}
				  
				
			}
			  
			  sink.listItem_();
			  sink.list_();  
//----------------------------------------------------------------------------------------------	
//-----------------------comment----------------------------------------------------------------
			  
			  sink.list();
			  sink.listItem();
			  sink.text(bundle.getString("report.onto.class_comment"));
			  
			  if(!cls.getValue().toString().isEmpty()){
				  
				  sink.text(" "+cls.getValue().toString());
			  }else{
				  
				  sink.text("");
			  }
			  
			  sink.listItem_();
			  sink.list_();  
			  
//--------------------------------------------------------------------------------------------			  
			  
			  
			  sink.rawText("</td>");
			  sink.rawText("</tr>");
			  
			  sink.table_();
			 }
			 
			//for (String obj : prepare.list_objectproperties()) {
		  for(Map.Entry object : prepare.list_objectproperties().entrySet()){	
			  
			  sink.table();
			  
			  sink.tableRow();
			  sink.tableHeaderCell();
			  sink.text(object.getKey().toString());
			  sink.tableHeaderCell_();
			  sink.tableRow_();
			  
			  sink.rawText("<tr id="+object.getKey()+">");
			  sink.rawText("<td>");
			
			  
			//----------------------------------------------------------------------------------------------------
			  sink.list();
			  sink.listItem();
			  sink.text(bundle.getString("report.onto.domain"));
			 
		      for (Map.Entry entry : ((Map<List<String>, List<String>>) object.getValue()).entrySet()) { 
				  
	
				  for(String dom : (List<String>) entry.getKey()){
					  
					  sink.link("#"+dom);
					  sink.text(" "+dom+"| ");
					  sink.link_();
					  
				  }			  
				  
			   }
			  
		      sink.listItem_();
			  sink.list_();
			  
//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			  sink.list();
			  sink.listItem();
			  sink.text(bundle.getString("report.onto.range"));
			  
		      for (Map.Entry entry : ((Map<List<String>, List<String>>) object.getValue()).entrySet()) { 
				  
				  for(String range : (List<String>) entry.getValue()){
					  
					  sink.link("#"+range);
					  sink.text(" "+range+"| ");
					  sink.link_();
					  
				  }			  
				  
			   }
			  
		      sink.listItem_();
			  sink.list_();
			  
//-----------------------comment----------------------------------------------------------------
			  
			  sink.list();
			  sink.listItem();
			  sink.text(bundle.getString("report.onto.class_comment"));
			  
			  
			  sink.text("");
		
			  sink.listItem_();
			  sink.list_();  
			  
//--------------------------------------------------------------------------------------------			  
			  
//----------------------------------------------------------------------------------------------------			  
			  
			  
			  
			  sink.rawText("</td>");
			  sink.rawText("</tr>");
			  
			  sink.table_();
			 }
		  
		  	//for (String data : prepare.list_dataproperties()) {
		  	
		  	for(Map.Entry data : prepare.list_dataproperties().entrySet()){	
			  
			  sink.table();
			  
			  sink.tableRow();
			  sink.tableHeaderCell();
			  
			  //sink.text(data);
			  sink.text(data.getKey().toString());
			  
			  sink.tableHeaderCell_();
			  sink.tableRow_();
			  
			  //sink.rawText("<tr id="+data+">");
			  sink.rawText("<tr id="+data.getKey()+">");
			  
			  sink.rawText("<td>");
			  
//----------------------------------------------------------------------------------------------------
			  sink.list();
			  sink.listItem();
			  sink.text(bundle.getString("report.onto.domain"));
			 
		      for (Map.Entry entry : ((Map<List<String>, List<String>>) data.getValue()).entrySet()) { 
				  
	
				  for(String dom : (List<String>) entry.getKey()){
					  
					  sink.link("#"+dom);
					  sink.text(" "+dom+"| ");
					  sink.link_();
					  
				  }			  
				  
			   }
			  
		      sink.listItem_();
			  sink.list_();
			  
//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
			  sink.list();
			  sink.listItem();
			  sink.text(bundle.getString("report.onto.range"));
			  
		      for (Map.Entry entry : ((Map<List<String>, List<String>>) data.getValue()).entrySet()) { 
				  
				  for(String range : (List<String>) entry.getValue()){
					  
					 // sink.link("#"+range);
					  sink.text(" "+range+"| ");
					 // sink.link_();
					  
				  }			  
				  
			   }
			  
		      sink.listItem_();
			  sink.list_();

			 
//----------------------------------------------------------------------------------------------------				  
//-----------------------comment----------------------------------------------------------------
			  
			  sink.list();
			  sink.listItem();
			  sink.text(bundle.getString("report.onto.class_comment"));
			  
			
			  sink.text("");
			
			  sink.listItem_();
			  sink.list_();  
			  
//--------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------------			  
			  
			
			  sink.rawText("</td>");
			  sink.rawText("</tr>");
			  
			  sink.table_();
			 }
		  
		  sink.section2_();
		  
	  }
	  
	//---------------------------------------------------------------------------------------------
	//---------------------------------------------------------------------------------------------
	public void generateVisualizer(String sourceDir, File vm_resource,File target){
		
		TechnicalReportTemplate template = new TechnicalReportTemplate(sourceDir, vm_resource, target);
		
		try {
			
			template.prepare();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        
		
			
		  doVizHead();
		  
		  
		  sink.rawText("<iframe src=\""+target.toURI().toString()+"\"width=\"100%\" height=\"650\"name=\"Visualizer\">");
		 
		  sink.rawText("</iframeset>");
		  
		  sink.body_();
		  sink.flush();
	}
	
	private void doVizHead(){
		
		  sink.head();
		  sink.title();
		  sink.text("Ontology Visualizer");
		  sink.title_();
		  sink.head_();
		  
		  sink.body();
		
		
	}
	  

}
