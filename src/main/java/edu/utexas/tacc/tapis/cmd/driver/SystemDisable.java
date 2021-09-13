package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.systems.client.SystemsClient;

public class SystemDisable 
{
	/** Disables a system 
     * 
     * SystemDisable -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -system <name of system>
     */
    public static void main(String[] args) throws Exception
    {
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemDisable has failed");
        }
    	
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for SystemDisable operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemDiable operation, THROWING ERROR");
    	
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        // Disable the System
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        int count = sysClient.disableSystem(parms.systemName);
        System.out.println("Systems updated: " + count);
        
        //expecting false to be returned, meaning the system has been properly disabled
        System.out.println("Result of isEnabled() test for system " + parms.systemName + " is: " + sysClient.isEnabled(parms.systemName));        
    }
}
