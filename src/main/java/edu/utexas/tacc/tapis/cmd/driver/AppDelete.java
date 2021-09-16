package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.apps.client.AppsClient;

public class AppDelete 
{
	/** Deletes a Tapis app 
	 *
     *  AppDelete -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -app <name of app>
     */
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for AppDelete has failed");
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.appName == null)
    		throw new Exception("appName is null and is required for AppDelete operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for AppDelete operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Delete the app.
        System.out.println("Deleting app " + parms.appName + ".");
        var appsClient = new AppsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	TestUtils.setOboHeaders(appsClient, parms.oboUser, parms.oboTenant);
  
        //----------------------- USE CLIENT OBJECT -----------------------//
        int count = appsClient.deleteApp(parms.appName);
    	System.out.println("Apps deleted: " + count);
	}
}
