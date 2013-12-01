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
 * @goal owltest
 * 
 * @phase test
 */
public class Main
    extends AbstractMojo
{
    /**
     * Location of the file.
     * @parameter 
	 * 		 expression="${test}"
	 *		 default-value="${project.basedir}/owl/camera.owl"
     * @required
     */
    private String owl_file;

    public void execute()
        throws MojoExecutionException
    {
     
		//owl_file="owl/camera.owl";
		
		System.out.println("------      "+owl_file+"     -------------------");
		
		List<String> syntax_message = new ArrayList<String>();
        List<String> consistent_message = new ArrayList<String>();
		
		SyntaxChecker syntax = new SyntaxChecker();
        
        syntax_message=syntax.checker(owl_file);
		
	    ConsistentChecker consist = new ConsistentChecker();
        
       consistent_message=consist.check(owl_file);
		
		this.getLog().info("_________________________________________________________________________");
		this.getLog().info("*************************************************************************");
		this.getLog().info("_________________________________________________________________________");
		this.getLog().info("=========TEST======================");
		this.getLog().info("  ");
		this.getLog().info("=========SyntaxTest================");
		this.getLog().info("   ");

		for (String message : syntax_message) {
        	
        	//System.out.println(message);
			this.getLog().info(message);
			
		}
		
		this.getLog().info("   ");
		this.getLog().info("=========ConsistentTest==============");
		this.getLog().info("   ");
        
		for (String message : consistent_message) {
        	
        	//System.out.println(message);
			this.getLog().info(message);
			
		}
		
		this.getLog().info("   ");
		this.getLog().info("_________________________________________________________________________");
		this.getLog().info("*************************************************************************");
		this.getLog().info("_________________________________________________________________________");
        
		
		
	}	
}
