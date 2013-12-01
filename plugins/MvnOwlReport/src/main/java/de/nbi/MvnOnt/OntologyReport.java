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
	import org.apache.maven.project.MavenProject;
	import org.apache.maven.reporting.AbstractMavenReport;
	import org.apache.maven.reporting.MavenReportException;
	import org.codehaus.doxia.site.renderer.SiteRenderer;



	import java.io.File;
	import java.util.Locale;
	import java.util.ResourceBundle;


	/**
	 * Perform Lines-Of-Code analysis, and generate report.
	 * 
	 * @goal ontologyreport
	 * @phase site
	 * 
	 */
	public class OntologyReport 
	    extends AbstractMavenReport
	{

		
		 /**
		   * The directory containing source code to parse.
		   *
		   * @parameter 
		   * 		 expression="${src}"
		   *		 default-value="owl/camera.owl"
		   * @required
		   */
		  private String sourceDir;
		  
		  /**
		   * The output directory.
		   *
		   * @parameter 
		   * 		 expression="${out}"
		   *		 default-value="${project.build.directory}/site"
		   * @required
		   */
		  private File outputDirectory;
		  
		  /**
		   * <i>Maven Internal</i>: The Doxia Site Renderer.
		   *
		   * @component
		   */
		  private SiteRenderer siteRenderer;
		  
		  /**
		   * <i>Maven Internal</i>: The Project descriptor.
		   * @parameter expression="${project}"
		   * @required
		   * @readonly
		   */
		  private MavenProject project;
		
		
		//  private static String sourceDir = "owl/camera.owl";
		  
		 // private static String sou = "src/main";

		
		
		public String getDescription(Locale locale) {
			// TODO Auto-generated method stub
			return getBundle(locale).getString("report.ontology.description");
		}

		public String getName(Locale locale) {
			// TODO Auto-generated method stub
			return getBundle(locale).getString("report.ontology.name");
		}

		
		public String getOutputName() {
			// TODO Auto-generated method stub
			
			
			return "owl-reporting";
		
		}



		@Override
		protected String getOutputDirectory() {
			// TODO Auto-generated method stub
			return outputDirectory.getAbsolutePath();
		}
		
		  public void setReportOutputDirectory(File reportOutputDirectory)
		  {
		    super.setReportOutputDirectory(reportOutputDirectory);
		    this.outputDirectory = reportOutputDirectory;
		  }
		
		@Override
		protected MavenProject getProject() {
			// TODO Auto-generated method stub
			return project;
		}

		@Override
		protected SiteRenderer getSiteRenderer() {
			// TODO Auto-generated method stub
			return siteRenderer;
		}
		
		 public boolean canGenerateReport()
		  {
			 
			File file = new File(sourceDir);
			
		    return file.exists();
		  }
		
		//-----------------------------------------------------------------------------------------------------
		private static ResourceBundle getBundle(Locale locale) {
			
			GenerateOWLPropertiesFile gen = new GenerateOWLPropertiesFile();
			gen.generate_property_file();

			return ResourceBundle.getBundle("owl-report", locale,OntologyReport.class.getClassLoader());
		 }
		//-------------------------------------------------------------------------------------------------------
		
		@Override
		protected void executeReport(Locale locale) throws MavenReportException {
			// TODO Auto-generated method stub
			
			GenerateOWLPropertiesFile gen = new GenerateOWLPropertiesFile();
			gen.generate_property_file();
			

			
			if(!canGenerateReport()){
				
				this.getLog().info("could not generate");
				
			}else{
			
				ResourceBundle bundle = getBundle(locale);
				
				
				generateMainReport(bundle);

				
			}
			
		}
		
		  // -----------------------------------------------------------------------------
		  private void generateMainReport(ResourceBundle bundle){
			  
			  OntologyReportGenerator generat = new OntologyReportGenerator(getSink(), bundle, sourceDir);
			  generat.generateReport();
			  
		  }

	   
	}
	
	
	
	
