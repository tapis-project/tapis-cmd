package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.apps.client.AppsClient;

public class AppDisable 
{
	/** Disables app 
     * 
     * AppDisable -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -app <name of app>
     */
	public static void main(String[] args) throws Exception
	{
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for AppDisable has failed");
        }
    	
    	if(parms.appName == null)
    		throw new Exception("appName is null and is required for AppDisable operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for AppDisable operation, THROWING ERROR");
    	
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        // Disable the app.
        System.out.println("Disabling app " + parms.appName + ".");
        var appsClient = new AppsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
    	int count = appsClient.disableApp(parms.appName);
    	System.out.println("Apps updated: " + count);
    	
    	//expecting false to be returned, meaning the app has been properly disabled
    	System.out.println("Result of isEnabled() test for " + parms.appName + "is: " + appsClient.isEnabled(parms.appName));
	}
}
