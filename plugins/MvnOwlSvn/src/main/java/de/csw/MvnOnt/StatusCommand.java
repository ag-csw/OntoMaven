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

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

//import java.io.File;
import java.io.FileWriter;
//import java.io.IOException;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;




/**
 * Goal which touches a timestamp file.
 *
 * @goal status
 * 
 * 
 */
public class StatusCommand
    extends AbstractMojo
{
    /**
     * Location of the file.
     * parameter expression="{wd}"
     * default-value="_empty"
     */
	//private String working_directory;
	
	//private String working_directory = "C:\\Users\\zehra\\Documents\\my_repository";
	
	String temp_directory = "C:\\Users\\zehra\\Documents\\temp";
	private	String repository ="file:///C:/my_repo//trunk//";
	//private String working_directory = "";

    public void execute()
        throws MojoExecutionException
    {	
		String working_directory="";
		
		//if(working_directory.equals("_empty")){
		
			ReadWDObject read = new ReadWDObject();
		
			working_directory = read.wd();
			
			
			ReadRepObject readrep = new ReadRepObject();
		
			repository = readrep.wd();
			
			
		
				
		//}
		
		
		
		CompareOntologies compare;
		
		System.out.println("------------------------------     STATUS INFORMATION    -------------------");
		
		Checkout checkout = new Checkout();
		
		checkout.checkoutTemp(temp_directory, repository);
		
		FileFinder finder = new FileFinder();
		
		Map<String,String> eq = new HashMap<String, String>(); 
		
		
		if(!finder.find(temp_directory).isEmpty()&&!finder.find(working_directory).isEmpty()){	
			
			
			for(File fl : finder.find(temp_directory)){
				
				if(fl.getName().endsWith(".owl")){
					
					for(File wf : finder.find(working_directory)){
						
						if(wf.getName().endsWith(".owl")){
							
							if(fl.getName().equals(wf.getName())){
								
								eq.put(fl.getAbsolutePath(),wf.getAbsolutePath());
								
							}
							
						}
						
					}
					
				}
			}
		}	
		
		
		
		if(!eq.isEmpty()){
			
			
			
			for (Map.Entry<String, String> map : eq.entrySet()) {
	
				compare = new CompareOntologies(map.getKey(), map.getValue());
				
				System.out.println(compare.load_compare("status"));
				
				
			}
			
		}
		
		
		
		//delete Temp directory
		
		DeleteTempFile del = new DeleteTempFile();
		
		del.delTemp(temp_directory);
		
		
	

	  
    }
}
