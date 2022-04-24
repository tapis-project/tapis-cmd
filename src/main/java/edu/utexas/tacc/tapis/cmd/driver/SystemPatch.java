package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;
import edu.utexas.tacc.tapis.systems.client.SystemsClient;
import edu.utexas.tacc.tapis.systems.client.gen.model.ReqPatchSystem;

public class SystemPatch
{
    /** Patches system 
     * 
     * SystemPatch -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -req <name of json request file located in driver/requests> -system <name of tapis system>
     */
    public static void main(String[] args) throws Exception
    {
    	//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemPatch has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.reqFilename == null)
    		throw new Exception("reqFilename is null and is required for SystemPatch operation, THROWING ERROR");
    	
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for SystemPatch operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemPatch operation, THROWING ERROR");
    	
        System.out.println("Processing " + parms.reqFilename + ".");
    	
        //----------------------- READ JSON REQUEST INTO REQ OBJECT -----------------------//
        // Convert json string into an app create request.
        ReqPatchSystem sysReq = TapisGsonUtils.getGson().fromJson(DriverUtils.readRequestFile(parms.reqFilename), ReqPatchSystem.class);
        
        //----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create the system.
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	DriverUtils.setOboHeaders(sysClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        var result = sysClient.patchSystem(parms.systemName, sysReq);
        sysClient.close();
        System.out.println(result);
        System.out.println("Finished processing " + parms.reqFilename);
    }
}
