package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.apps.client.AppsClient;


public class AppGetLatestVersion 
{
	/** Gets the latest version of an app 
     * 
     * AppGetLatestVersion -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -app <name of app>
     */
	public static final boolean _requireExecPerm = true;
	
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for AppGetLatestVersion has failed");
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.appName == null)
    		throw new Exception("appName is null and is required for AppGetLatestVersion operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for AppGetLatestVersion operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);

        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Get the latest app version.
        System.out.println("Getting latest version of " + parms.appName + ".");
        var appsClient = new AppsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	TestUtils.setOboHeaders(appsClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        var app = appsClient.getAppLatestVersion(parms.appName,_requireExecPerm,"");
        System.out.println(app.toString());
	}
}
