package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.systems.client.SystemsClient;
import edu.utexas.tacc.tapis.systems.client.SystemsClient.AuthnMethod;

public class SystemGetAsJobs 
{	
	/** Gets system as jobs on behalf of another user 
     * 
     * SystemGetAsJobs -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -system <name of system> -oboUser <name of obo user> -oboTenant <name of obo tenant>
     */
    public static void main(String[] args) throws Exception
    {
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemGetAsJobs has failed");
        }
    	
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for SystemGetAsJobs operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemGetAsJobs operation, THROWING ERROR");
        
    	if(parms.oboUser == null)
    		throw new Exception("oboUser is null and is required for SystemGetAsJobs operation, THROWING ERROR");
    	
    	if(parms.oboTenant == null)
    		throw new Exception("oboUser is null and is required for SystemGetAsJobs operation, THROWING ERROR");
    	
        // Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        // Get the system.
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        
        //get the obo parts and assign them after running check
        sysClient.addDefaultHeader("X-TAPIS-TENANT", parms.oboTenant);
        sysClient.addDefaultHeader("X-TAPIS-USER", parms.oboUser);
        
        Boolean returnCreds = Boolean.TRUE;
        Boolean checkExec   = Boolean.FALSE;
        AuthnMethod  defaultAuthMethod = null;
        var sys = sysClient.getSystem(parms.systemName, returnCreds, defaultAuthMethod, checkExec);
        System.out.println(sys.toString());
    }
}
