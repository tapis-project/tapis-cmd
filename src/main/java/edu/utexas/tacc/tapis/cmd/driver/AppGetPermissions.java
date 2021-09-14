package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.apps.client.AppsClient;

public class AppGetPermissions 
{
	/** Gets the permissions for a user on an app
     * 
     * AppGetPermissions -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -app <name of app> -user <username for which to get permissions>
     */
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemEnable has failed");
        }
		
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.appName == null)
    		throw new Exception("appName is null and is required for AppGetPermissions operation, THROWING ERROR");
    	
    	if(parms.userName == null)
    		throw new Exception("userName is null and is required for AppGetPermissions operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for AppGetPermissions operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE AND USE CLIENT OBJECT -----------------------//
        // Get the app permissions
        System.out.println("Getting permissions for app " + parms.appName + " with username "+ parms.userName +".");
        var appsClient = new AppsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        var app = appsClient.getAppPermissions(parms.appName, parms.userName);
        System.out.println(app.toString());
	}
}
