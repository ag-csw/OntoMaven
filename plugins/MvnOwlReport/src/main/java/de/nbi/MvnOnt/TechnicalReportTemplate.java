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
import java.io.FileWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import de.nbi.MvnOnt.data.OwlData;


	public class TechnicalReportTemplate {
		
		private File vm_resource;
		private String owl_file_location;
		private File target;
		
		private OwlData owldata ;
		
		private List<String> list_all = new ArrayList<String>();
		
		
		String class_name;
		String subclass_name;
		
		public TechnicalReportTemplate(String owl_file_lcation ,File vm_resource,File target){
			
			this.vm_resource = vm_resource;
			this.owl_file_location = owl_file_lcation;
			this.target = target;
			
		}
		

		void prepare() throws Exception{
		
			
			
			if(vm_resource.exists()){
			
				Map<String,String> map_sub = new HashMap<String,String>();
				
			
				owldata = new OwlData(owl_file_location);
			
				
				Map<String,Map<String,String>> test_map = new HashMap<String,Map<String,String>>();
				
				
				test_map=owldata.sub_cls();
				
				
				String line;
				String with_out_thing;
				String change_type;
				
			
				for (Map.Entry map : test_map.entrySet()) {
					
						map_sub = (Map)map.getValue();
						
						for (Map.Entry map_two : map_sub.entrySet()) {
							
							if(map.getKey().equals("Thing")){
								
								with_out_thing = map_two.getKey().toString();
								change_type = "";
							}else {
								
								with_out_thing = map.getKey().toString();
								change_type = map_two.getValue().toString();
							}
							
							line = "{source:\""+with_out_thing+"\", target:\""+map_two.getKey().toString()+"\",type:\""+change_type+"\"},";
							
							list_all.add(line);
							
						}
					}
				
			
				
				change_type ="";	
				for(String cl : owldata.list_owlclasses()){
					
					line = "{source:\""+cl+"\", target:\""+cl+"\",type:\""+change_type+"\"},";
					
					list_all.add(line);
					
				}
				
				
				
			
				Map<String,String> data_property = new HashMap<String, String>();
				
				data_property = owldata.getDataProperty();
				
				Map<String,String> object_property = new HashMap<String, String>();
				
				object_property = owldata.getObjectProperty();
				
				//System.out.println(data_property);
				
	
				String visualizer_vm ="src/main/java/vm_resource/visualizer.vm";
				String index_vm ="src/main/java/vm_resource/index.vm";
				String wz_tooltip_js = "src/main/java/vm_resource/wz_tooltip.js";
				
					
					
					File wz = new File("target/my_resource/js/");
			      
					if(!wz.exists()){
			    	  
						wz.mkdirs();
					}
					
				
					VelocityEngine engin = new VelocityEngine();
			        
			        engin.init();
			        
//			        Template tempe = engin.getTemplate("src/test/test.vm");
			        
			        Template tempe = engin.getTemplate(visualizer_vm);
			        
			        VelocityContext context = new VelocityContext();

			        
			        context.put("list", list_all);
			        
			        StringWriter writer = new StringWriter();
			        
			        tempe.merge(context, writer);
			        
				  
				  Writer file = new FileWriter(new File("target/my_resource/js/visualizer.js"));
				  
			      tempe.merge(context, file);
			      //template.process(map_test, file);
			      file.flush();
			      file.close();
			      
			      //------------------------------------------------------------------------
			      
			     
			      
			      
			      Path copySourcePath = Paths.get(wz_tooltip_js);
			      Path copyTargetPath = Paths.get("target/my_resource/js/wz_tooltip.js");
			      
			      Files.copy(copySourcePath, copyTargetPath);
			      
			      //------------------------------------------------------------------------
			      
		      
//			      tempe = engin.getTemplate("src/test/index.vm");
			      tempe = engin.getTemplate(index_vm);
			      
			      context.put("data_map", data_property);
				  
				  context.put("object_map", object_property);
			      
			      writer = new StringWriter();
			      tempe.merge(context, writer);
			      
			   
				 
				 
			      Writer file2 = new FileWriter(target);
			      tempe.merge(context, file2);
			      //template.process(map_test, file);
			      file2.flush();
			      file2.close(); 
		
		}else{
			System.out.println("velocity template resource don't exist");
		}
			
	}

}


