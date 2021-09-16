package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.systems.client.SystemsClient;

public class SystemDeleteSchedulerProfile 
{	
	/** Creates a system scheduler profile
     * 
     * SystemDeleteSchedulerProfile -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -scheduler <name of scheduler profile>
     */
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemGetSchedulerProfile has failed");
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemGetSchedulerProfile operation, THROWING ERROR");
    	
    	if(parms.schedulerName == null)
    		throw new Exception("schedulerName is null and is required for SystemGetSchedulerProfile operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Get the scheduler profiles 
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	TestUtils.setOboHeaders(sysClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        var sys = sysClient.deleteSchedulerProfile(parms.schedulerName);
        if(sys != -1)
        	System.out.println("Scheduler Profile: " + parms.schedulerName + " has been deleted");
	}
}
