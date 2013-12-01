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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Goal which touches a timestamp file.
 *
 * @goal owlentailment
 * 
 * @phase test
 */
public class Main
    extends AbstractMojo
{
    /**
     * Location of the file.
     * @parameter 
	 * 		 expression="${pre}"
	 *		 default-value="${project.basedir}/owl/premisse.owl"
     * @required
     */
    private String premise_file;
	
	
	  /**
     * Location of the file.
     * @parameter 
	 * 		 expression="${conc}"
	 *		 default-value="${project.basedir}/owl/conclusion.owl"
     * @required
     */
    private String conclusion_file;

    public void execute()
        throws MojoExecutionException
    {
     
		//owl_file="owl/camera.owl";
		
		//List<String> syntax_message = new ArrayList<String>();
       // List<String> consistent_message = new ArrayList<String>();
		
		//String premise_location="owl/1a.owl";
		//String conclusion_location ="owl/1a-conclusion.owl";
		
		//this.getLog().info(premise_location);
		
		File premiseOntologyFile = new File(premise_file);
        File conclusionOntologyFile = new File(conclusion_file);
		
		String result = null;
		
		try {
			
		
			EntailmentCheck entail = new EntailmentCheck();
			result = entail.checkEntailment(premiseOntologyFile,conclusionOntologyFile);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		this.getLog().info("_________________________________________________________________________");
		this.getLog().info("*************************************************************************");
		this.getLog().info("_________________________________________________________________________");
		this.getLog().info("===========================ENTAILMENT-TEST=============================");
		this.getLog().info("  ");
		
	
			this.getLog().info("|----------------------------------------------------------|");
			this.getLog().info("|                                                          |");
			
			this.getLog().info("|                       "+result+"                       |");
			
			this.getLog().info("|                                                          |");
			this.getLog().info("|----------------------------------------------------------|");
			
			
		this.getLog().info("   ");
		this.getLog().info("_________________________________________________________________________");
		this.getLog().info("*************************************************************************");
		this.getLog().info("_________________________________________________________________________");
        
		
		
	}	
}
