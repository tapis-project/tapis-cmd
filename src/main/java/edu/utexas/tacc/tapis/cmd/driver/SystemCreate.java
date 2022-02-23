package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;
import edu.utexas.tacc.tapis.systems.client.SystemsClient;
import edu.utexas.tacc.tapis.systems.client.gen.model.ReqPostSystem;
import edu.utexas.tacc.tapis.systems.client.gen.model.ReqPostCredential;
public class SystemCreate
{	
    /** Creates a system
     * 
     * SystemCreate -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -req <name of json request file located in driver/requests>
     * -pubKey <full path to public key> -privKey <full path to private key>
     */
    public static void main(String[] args) throws Exception
    {
    	//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemCreate has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.reqFilename == null)
    		throw new Exception("reqFilename is null and is required for SystemCreate operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("reqFilename is null and is required for SystemCreate operation, THROWING ERROR");
    	
    	if(parms.pubKey == null)
    		throw new Exception("pubKey is null and is required for SystemCreate operation, THROWING ERROR");
    	
    	if(parms.privKey == null)
    		throw new Exception("privKey is null and is required for SystemCreate operation, THROWING ERROR");
    	
        System.out.println("Processing " + parms.reqFilename + ".");
        
        //----------------------- READ JSON REQUEST INTO REQ OBJECT -----------------------//
        // Convert json string into an app create request.
        ReqPostSystem sysReq = TapisGsonUtils.getGson().fromJson(DriverUtils.readRequestFile(parms.reqFilename), ReqPostSystem.class);
        
        //----------------------- RETRIEVE AND ASSIGN PUB AND PRIV KEYS -----------------------//
        if(sysReq.getDefaultAuthnMethod().toString() == "PKI_KEYS") 
        {
            ReqPostCredential credReq = new ReqPostCredential();
            credReq.setPrivateKey(DriverUtils.getCredFile(parms.privKey));
            credReq.setPublicKey(DriverUtils.getCredFile(parms.pubKey));
            sysReq.setAuthnCredential(credReq);
        }
        
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
        sysClient.createSystem(sysReq);
        System.out.println("Finished processing " + parms.reqFilename);
    }
}
