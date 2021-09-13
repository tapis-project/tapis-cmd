package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.apps.client.AppsClient;

public class AppCheckReady 
{
	/** Checks app readiness
     * 
     * AppCheckReady -jwt <jwt filename located in $HOME/Tapis-cmd/jwt>
     */
	
	public static void main(String[] args) throws Exception
	{
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for AppCheckReady has failed");
        }
		
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for AppCheckReady operation, THROWING ERROR");
    	
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        // Check App Health
        var appsClient = new AppsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        System.out.println("Result of check ready: " + appsClient.checkReady());
	}
}
