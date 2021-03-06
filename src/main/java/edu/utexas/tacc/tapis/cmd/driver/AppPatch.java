package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.apps.client.AppsClient;
import edu.utexas.tacc.tapis.apps.client.gen.model.ReqPatchApp;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;

public class AppPatch 
{
    /** Patches an app 
     * 
     *  AppPatch -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -app <name of app> -req <name of json request file located in driver/requests> 
     *  -appVersion <version of app>
     */
    public static void main(String[] args) throws Exception
    {
    	//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for AppPatch has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.reqFilename == null)
    		throw new Exception("reqFilename is null and is required for AppPatch operation, THROWING ERROR");
    	
    	if(parms.appName == null)
    		throw new Exception("appName is null and is required for AppPatch operation, THROWING ERROR");
    	
    	if(parms.appVersion == null)
    		throw new Exception("appVersion is null and is required for AppPatch operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for AppPatch operation, THROWING ERROR");
    	
        System.out.println("Processing " + parms.reqFilename + ".");
        
        //----------------------- READ JSON REQUEST INTO REQ OBJECT -----------------------//
        // Convert json string into an app create request
        ReqPatchApp payload = TapisGsonUtils.getGson().fromJson(DriverUtils.readRequestFile(parms.reqFilename), ReqPatchApp.class);
    	
        //----------------------- READ IN JWT PROFILE -----------------------//
        //Read base url and jwt from file
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create the app.
        var appsClient = new AppsClient(props.getProperty("BASE_URL"),props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	DriverUtils.setOboHeaders(appsClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        appsClient.patchApp(parms.appName, parms.appVersion, payload);
        System.out.println("Finished processing " + parms.reqFilename);
    }
}
