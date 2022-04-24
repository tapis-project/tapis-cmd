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
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for AppCheckReady has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for AppCheckReady operation, THROWING ERROR");

    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename);

        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Check App Health
        var appsClient = new AppsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	DriverUtils.setOboHeaders(appsClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        System.out.println("Result of check ready: " + appsClient.checkReady());
	}
}
