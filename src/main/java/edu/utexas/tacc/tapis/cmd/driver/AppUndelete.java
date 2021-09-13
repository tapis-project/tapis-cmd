package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.apps.client.AppsClient;

public class AppUndelete 
{
	/** Undelete an app
     * 
     * AppUndelete -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -app <name of app>
     */
	public static void main(String[] args) throws Exception
	{
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for AppUndelete has failed");
        }
    	
    	if(parms.appName == null)
    		throw new Exception("appName is null and is required for AppUndelete operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for AppUndelete operation, THROWING ERROR");
    	
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        // Undelete the app.
        System.out.println("Undeleting app " + parms.appName + ".");
        var appsClient = new AppsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
    	int count = appsClient.undeleteApp(parms.appName);
    	System.out.println("Apps updated: " + count);
	}
}
