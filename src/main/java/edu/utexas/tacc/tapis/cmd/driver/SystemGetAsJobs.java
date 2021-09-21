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
    	//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemGetAsJobs has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for SystemGetAsJobs operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemGetAsJobs operation, THROWING ERROR");
        
    	if(parms.oboUser == null)
    		throw new Exception("oboUser is null and is required for SystemGetAsJobs operation, THROWING ERROR");
    	
    	if(parms.oboTenant == null)
    		throw new Exception("oboUser is null and is required for SystemGetAsJobs operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Get the system.
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	TestUtils.setOboHeaders(sysClient, parms.oboUser, parms.oboTenant);
        
        Boolean returnCreds = Boolean.TRUE;
        Boolean checkExec   = Boolean.FALSE;
        AuthnMethod  defaultAuthMethod = null;
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        var sys = sysClient.getSystem(parms.systemName, returnCreds, defaultAuthMethod, checkExec);
        System.out.println(sys.toString());
    }
}
