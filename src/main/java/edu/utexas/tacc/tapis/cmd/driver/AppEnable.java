package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.apps.client.AppsClient;

public class AppEnable
{
	/** Enables app given an app name and a jwt filename
     * 
	 * AppEnable -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -app <name of app>
     */
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for AppEnable has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.appName == null)
    		throw new Exception("appName is null and is required for AppEnable operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for AppEnable operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Enable the app
        System.out.println("Enabling app " + parms.appName + ".");
        var appsClient = new AppsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	TestUtils.setOboHeaders(appsClient, parms.oboUser, parms.oboTenant);
       
        //----------------------- USE CLIENT OBJECT -----------------------//
        int count = appsClient.enableApp(parms.appName);
    	System.out.println("Apps updated: " + count);
    	
    	//expecting true to be returned, meaning the app has been properly enabled
    	System.out.println("Result of isEnabled() test for " + parms.appName + "is: " + appsClient.isEnabled(parms.appName));
	}
}
