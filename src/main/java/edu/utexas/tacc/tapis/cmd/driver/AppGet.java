package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.apps.client.AppsClient;

public class AppGet 
{	
	/** Gets an app given 
     * 
     * AppGet -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -app <name of app> -appVersion <version of app>
     */
	public static void main(String[] args) throws Exception
    {
		//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for AppGet has failed");
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.appName == null)
    		throw new Exception("appName is null and is required for AppGet operation, THROWING ERROR");
    	
    	if(parms.appVersion == null)
    		throw new Exception("appVersion is null and is required for AppGet operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for AppGet operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Get the app.
        System.out.println("Retrieving app " + parms.appName + " version " + parms.appVersion + ".");
        var appsClient = new AppsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	TestUtils.setOboHeaders(appsClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        var app = appsClient.getApp(parms.appName, parms.appVersion);
        System.out.println(app.toString());
    }
}
