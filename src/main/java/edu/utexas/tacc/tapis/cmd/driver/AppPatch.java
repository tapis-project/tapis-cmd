package edu.utexas.tacc.tapis.cmd.driver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.apps.client.AppsClient;
import edu.utexas.tacc.tapis.apps.client.gen.model.ReqPatchApp;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;

public class AppPatch 
{
    // Subdirectory relative to current directory where request files are kept.
    private static final String REQUEST_SUBDIR = "requests";
    
    /** Patches an app 
     * 
     *  AppPatch -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -app <name of app> -req <name of json request file located in driver/requests> 
     *  -appVersion <version of app>
     */
    public static void main(String[] args) throws Exception
    {
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for AppPatch has failed");
        }
    	
    	if(parms.reqFilename == null)
    		throw new Exception("reqFilename is null and is required for AppPatch operation, THROWING ERROR");
    	
    	if(parms.appName == null)
    		throw new Exception("appName is null and is required for AppPatch operation, THROWING ERROR");
    	
    	if(parms.appVersion == null)
    		throw new Exception("appVersion is null and is required for AppPatch operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for AppPatch operation, THROWING ERROR");
    	
    	// Get the current directory.
        String curDir = System.getProperty("user.dir");
        String reqDir = curDir + "/" + REQUEST_SUBDIR;
        
        //pull in request filename parameter obj. and append to REQUEST_SUBDIR for request Path obj.
        String reqSuffix = parms.reqFilename;
        String request = reqDir+"/"+reqSuffix;
        Path req = Path.of(request);
        String reqString = Files.readString(req);
        
        // Informational message.
        System.out.println("Processing " + req.toString() + ".");
        
        // Convert json string into an app create request
        ReqPatchApp payload = TapisGsonUtils.getGson().fromJson(reqString, ReqPatchApp.class);
    	
        //Read base url and jwt from file
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        // Create the app.
        var appsClient = new AppsClient(props.getProperty("BASE_URL"),props.getProperty("USER_JWT"));
        appsClient.patchApp(parms.appName, parms.appVersion, payload);
        System.out.println("Finished processing " + req.toString() + ".");
    }
}
