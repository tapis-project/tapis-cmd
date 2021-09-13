package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.systems.client.SystemsClient;

public class SystemGet 
{
	/** Gets system 
     * 
     * SystemGet -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -system <name of system>
     */
	public static void main(String[] args) throws Exception
    {
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemGet has failed");
        }
    	
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for SystemGet operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemGet operation, THROWING ERROR");
    	
        // Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        // Create the app.
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        var sys = sysClient.getSystem(parms.systemName);
        System.out.println(sys.toString());
    }
}
