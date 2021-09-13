package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.apps.client.AppsClient;

public class AppsGet 
{
	/** Gets all the apps available to a jwt
     * 
     * AppsGet -jwt <jwt filename located in $HOME/Tapis-cmd/jwt>
     */
	public static void main(String[] args) throws Exception
	{
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for AppsGet has failed");
        }
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for AppsGet operation, THROWING ERROR");
    	
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        // Create the app.
        System.out.println("Retrieving all apps accessible to the caller");
        var appsClient = new AppsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        var apps = appsClient.getApps();
        System.out.println(apps.toString());
	}
}
