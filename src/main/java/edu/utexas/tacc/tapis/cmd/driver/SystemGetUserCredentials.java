package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.systems.client.SystemsClient;

public class SystemGetUserCredentials 
{
	/** Gets system specific user credentials on a system
     * 
     *  SystemGetUserCredentials -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -system <name of system> -oboUser <name of obo user> -oboTenant <name of obo tenant>
     *  -user <name of user>
     */

	public static void main(String[] args) throws Exception
	{
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemGetUserCredentials has failed");
        }
		
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for SystemGetUserCredentials operation, THROWING ERROR");
    	
    	if(parms.oboUser == null)
    		throw new Exception("oboUser is null and is required for SystemGetAsJobs operation, THROWING ERROR");
    	
    	if(parms.oboTenant == null)
    		throw new Exception("oboUser is null and is required for SystemGetAsJobs operation, THROWING ERROR");
    	
    	if(parms.userName == null)
    		throw new Exception("userName is null and is required for SystemGetUserCredentials operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemGetUserCredentials operation, THROWING ERROR");
    	
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
		
        // Get User Credentials
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //get the obo parts and assign them after running check
        sysClient.addDefaultHeader("X-TAPIS-TENANT", parms.oboTenant);
        sysClient.addDefaultHeader("X-TAPIS-USER", parms.oboUser);
      
        var sysUserCreds = sysClient.getUserCredential(parms.systemName, parms.userName);
        System.out.println(sysUserCreds.toString());
	}
}
