package edu.utexas.tacc.tapis.cmd.driver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;
import edu.utexas.tacc.tapis.systems.client.SystemsClient;
import edu.utexas.tacc.tapis.systems.client.gen.model.Credential;

public class SystemUpdateCreds
{
    // Subdirectory relative to current directory where request files are kept.
    private static final String REQUEST_SUBDIR = "requests";
    
    /** Updates system credentials
     * 
     * SystemUpdateCreds -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -req <name of json request file located in driver/requests> 
     * -system <name of tapis system> -user <name of user>
     */
    public static void main(String[] args) throws Exception
    {
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemUpdateCreds has failed");
        }
	
    	if(parms.reqFilename == null)
    		throw new Exception("reqFilename is null and is required for SystemUpdateCreds operation, THROWING ERROR");
    	
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for SystemUpdateCreds operation, THROWING ERROR");
    	
    	if(parms.userName == null)
    		throw new Exception("userName is null and is required for SystemUpdateCreds operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemUpdateCreds operation, THROWING ERROR");
    	
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
        // System.out.println(reqString);
        
        // Convert json string into an app create request.
        Credential creds = TapisGsonUtils.getGson().fromJson(reqString, Credential.class);
        
        // Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        // Update the credentials.
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        sysClient.updateUserCredential(parms.systemName, parms.userName, SystemsClient.buildReqCreateCredential(creds));
        sysClient.close();
    }
}
