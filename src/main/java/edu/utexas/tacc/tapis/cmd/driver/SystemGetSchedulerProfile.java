package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.systems.client.SystemsClient;

public class SystemGetSchedulerProfile 
{
	/** get system scheduler profile
	 * 
	 * SystemGetSchedulerProfile -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -scheduler <name of scheduler profile>
	 */
	public static void main(String[] args) throws Exception
	{
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemGetSchedulerProfile has failed");
        }
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemGetSchedulerProfile operation, THROWING ERROR");
    	
    	if(parms.schedulerName == null)
    		throw new Exception("schedulerName is null and is required for SystemGetSchedulerProfile operation, THROWING ERROR");
    	
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        // Create the app.
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        var sys = sysClient.getSchedulerProfile(parms.schedulerName);
        System.out.println(sys.toString());
	}
}
