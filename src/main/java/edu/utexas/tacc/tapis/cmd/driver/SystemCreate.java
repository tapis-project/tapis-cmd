package edu.utexas.tacc.tapis.cmd.driver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;
import edu.utexas.tacc.tapis.systems.client.SystemsClient;
import edu.utexas.tacc.tapis.systems.client.gen.model.ReqCreateSystem;
import edu.utexas.tacc.tapis.systems.client.gen.model.ReqCreateCredential;
public class SystemCreate
{	
    // Subdirectory relative to current directory where request files are kept.
    private static final String REQUEST_SUBDIR = "requests";
    
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
          throw new Exception("Parms initialization for SystemCreate has failed");
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
    	
    	//----------------------- CONFIGURE REQUEST FILE PATH -----------------------//
        // Get the current directory.
        String curDir = System.getProperty("user.dir");
        String reqDir = curDir + "/" + REQUEST_SUBDIR;
        
        //pull in request filename parameter obj. and append to REQUEST_SUBDIR for request Path obj.
        String reqSuffix = parms.reqFilename;
        String request = reqDir+"/"+reqSuffix;
        Path req = Path.of(request);
        String reqString = Files.readString(req);
        
        // Informational message.
        System.out.println("Processing " + req.toString() + ".");
        
        //----------------------- READ JSON REQUEST INTO REQ OBJECT -----------------------//
        // Convert json string into an app create request.
        ReqCreateSystem sysReq = TapisGsonUtils.getGson().fromJson(reqString, ReqCreateSystem.class);
        
        //----------------------- RETRIEVE AND ASSIGN PUB AND PRIV KEYS -----------------------//
        if(sysReq.getDefaultAuthnMethod().toString() == "PKI_KEYS") 
        {
            ReqCreateCredential credReq = new ReqCreateCredential();
            credReq.setPrivateKey(TestUtils.getCredFile(parms.privKey));
            credReq.setPublicKey(TestUtils.getCredFile(parms.pubKey));
            sysReq.setAuthnCredential(credReq);
        }
        
        //----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create the system.
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	TestUtils.setOboHeaders(sysClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        sysClient.createSystem(sysReq);
        System.out.println("Finished processing " + req.toString() + ".");
    }
}
