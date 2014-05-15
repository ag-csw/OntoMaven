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


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;



/**
 * Goal which touches a timestamp file.
 *
 * @goal repository
 * 
 * 
 */
public class RepDirectoryObject
    extends AbstractMojo
{
    /**
     * Location of the file.
     * @parameter expression="${rd}"
     * @required
     */
	 private String working_directory;
	
	private String object_directory = "src/resource/";
	

    public void execute()
        throws MojoExecutionException
		
    {
		File fl = new File(object_directory);
		
		if(!fl.exists()){
			fl.mkdirs();
		}
		
		
		OutputStream fos = null;
		ObjectOutputStream o = null;		
		

		try
		{
		  fos = new FileOutputStream( "src/resource/rep.ser");
		  o = new ObjectOutputStream( fos );
		  o.writeObject(working_directory );
		 
		}
		catch ( IOException e ) { 
			System.err.println( e ); 
			}
		finally { 
			
			try {
				fos.close(); 
				o.close();
			} catch ( Exception e ) {
				e.printStackTrace(); 
				} 
			}
		

	  
    }
}
