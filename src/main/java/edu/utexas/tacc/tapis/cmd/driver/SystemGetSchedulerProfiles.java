package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.systems.client.SystemsClient;

public class SystemGetSchedulerProfiles 
{
	/** get system scheduler profiles
	 * 
	 * SystemGetSchedulerProfiles -jwt <jwt filename located in $HOME/Tapis-cmd/jwt>
	 */
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemGetSchedulerProfiles has failed");
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemGetSchedulerProfiles operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE AND USE CLIENT OBJECT -----------------------//
        // Get the scheduler profiles 
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        var sys = sysClient.getSchedulerProfiles();
        System.out.println(sys.toString());
	}
}
