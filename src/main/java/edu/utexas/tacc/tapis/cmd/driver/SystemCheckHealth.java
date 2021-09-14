package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.systems.client.SystemsClient;

public class SystemCheckHealth 
{
	/** Checks system health 
     * 
     * SystemCheckHealth -jwt <jwt filename located in $HOME/Tapis-cmd/jwt>
     */
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemCheckHealth has failed");
        }
		
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemCheckHealth, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE AND USE CLIENT OBJECT -----------------------//
        // Check Health
        var systemsClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        System.out.println("Result of check health: " + systemsClient.checkHealth());
	}
}
