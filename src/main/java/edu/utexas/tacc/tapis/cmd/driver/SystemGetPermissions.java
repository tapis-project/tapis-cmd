package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.systems.client.SystemsClient;

public class SystemGetPermissions 
{
	/** Gets system permissions 
     * 
     * SystemGetPermissions -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -system <name of system> -user <name of tapis user> 
     */
	
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemGetPermissions has failed");
        }
		
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for SystemGetPermissions operation, THROWING ERROR");
    	
    	if(parms.userName == null)
    		throw new Exception("userName is null and is required for SystemGetPermissions operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemGetPermissions operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE AND USE CLIENT OBJECT -----------------------//
        // Get Permissions
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
    	var sysPerm = sysClient.getSystemPermissions(parms.systemName, parms.userName);
    	System.out.println("Permissions for user: " + parms.userName + ", on system: " + parms.systemName + ", include: " + sysPerm.toString());
	}
}
