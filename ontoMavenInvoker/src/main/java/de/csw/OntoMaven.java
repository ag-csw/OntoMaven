package de.csw;

import org.apache.maven.model.Build;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.shared.invoker.*;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import java.io.*;
import java.util.*;


abstract class OntoMaven
{
    protected Model model;
    protected Plugin plugin;
    protected boolean printOut;
    protected HashMap<String, String> configurations;
    protected LinkedList<String> userAspects;

    public OntoMaven(){

        model = new Model();
        model.setModelVersion("4.0.0");
        model.setGroupId( "TestGroupArtifactID" );
        model.setArtifactId("TestGroupArtifactName");
        model.setVersion("1.0.0");

        plugin = new Plugin();
        // TODO: get version from pom.xml
        plugin.setGroupId("de.csw");
        plugin.setArtifactId("ontomaven");
        plugin.setVersion("1.0-SNAPSHOT");

        configurations = new HashMap<>();
        userAspects = new LinkedList<>();

        printOut = false;
    }

    /**
     * prints out output for maven goals when they are executed
     */
    public void printOutputOn(){
        printOut = true;
    }

    /**
     * will print out no maven output
     */
    public void pringOutputOff(){
        printOut = false;
    }

    /**
     * overrides configurations for maven goal
     * @param config configurations as HashMap
     */
    public void overrideConfigurations(HashMap<String, String> config){
        configurations = config;
    }

    /**
     * adds one configuration line
     * @param configKey
     * @param configValue
     */
    public void addConfiguration(String configKey, String configValue){
        configurations.put(configKey,configValue);
    }

    /**
     * removes one specific configuration
     * @param configKey key of the configuration to be removed
     */
    public void removeConfiguration(String configKey){
        configurations.remove(configKey);
    }

    /**
     * overrides the current userAspects list
     * @param uAspects new userAspect list
     */
    public void overrideUserAspects(LinkedList<String> uAspects){ userAspects = uAspects;}

    /**
     * add one userAspect to the userAspect list
     * @param aspect userAspect to be added
     */
    public void addUserAspect(String aspect){ userAspects.add(aspect); }


    // generate configurations for pom file
    private Xpp3Dom generateConfig(){
        Xpp3Dom configuration = new Xpp3Dom( "configuration" );

        Iterator it = configurations.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry)it.next();
            Xpp3Dom configLine = new Xpp3Dom( (String) pair.getKey() );
            configLine.setValue((String) pair.getValue());
            configuration.addChild(configLine);
        }

        if(userAspects.size()>0){
            Xpp3Dom uAsp = new Xpp3Dom("userAspects");
            for(int i = 0; i < userAspects.size(); i++){
                Xpp3Dom aspect = new Xpp3Dom("aspect");
                aspect.setValue(userAspects.get(i));
                uAsp.addChild(aspect);
            }
            configuration.addChild(uAsp);
        }

        return configuration;
    }

    // generate userAspects for pom file
    private String generateUserAspects() {

        String uAsp = " \n<userAspects>\n";
        for(int i = 0; i < userAspects.size(); i++){
            uAsp = uAsp + "<aspect>" + userAspects.get(i) + "</aspect> \n" ;
        }
        uAsp = uAsp + "</userAspects> \n";
        return uAsp;
    }

    protected void executeGoal(String goal, HashMap<String, String> properties) throws IOException, MavenInvocationException {
        // get basic model and plugin setup for pom.xml
        Model pom = model.clone();
        Plugin ontoPlugin = plugin.clone();

        // set properties
        Iterator it = properties.entrySet().iterator();
        while(it.hasNext()){
            HashMap.Entry pair = (HashMap.Entry) it.next();
            pom.addProperty((String) pair.getKey(),(String) pair.getValue());
        }

        // set the plugin configuration
        ontoPlugin.setConfiguration(generateConfig());


        // build the complete model with plugin for pom.xml
        Build build = new Build();
        build.addPlugin( ontoPlugin);
        pom.setBuild( build );

        // write pom file
        Writer writer = new FileWriter("testPom.xml");
        new MavenXpp3Writer().write(writer, pom );
        writer.close();

        // set up maven invoker
        InvocationRequest request = new DefaultInvocationRequest();
        File pomFile = new File( "testPom.xml" );
        request.setPomFile( pomFile );
        request.setGoals( Collections.singletonList( goal ) );
        Invoker invoker = new DefaultInvoker();

        // set input stream Null
        request.setInputStream(InputStream.nullInputStream());
        // set up output handler if printOut it set
        if (!printOut){
            invoker.setOutputHandler(null);
        }

        // execute maven goal
        InvocationResult result = invoker.execute( request );
        pomFile.delete();

        if ( result.getExitCode() != 0 ) {

            throw new IllegalStateException( "Execution of Maven goal " + goal + " failed." );
        }


    }

    public abstract void execute() throws IOException, MavenInvocationException;
}
