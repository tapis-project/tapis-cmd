package edu.utexas.tacc.tapis.cmd.driver;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;
import edu.utexas.tacc.tapis.systems.client.SystemsClient;
import edu.utexas.tacc.tapis.systems.client.gen.model.ReqCreateSchedulerProfile;

public class SystemCreateSchedulerProfile 
{
	// Subdirectory relative to current directory where request files are kept.
    private static final String REQUEST_SUBDIR = "requests";

    /** Creates a system scheduler profile
     * 
     * SystemCreateSchedulerProfile -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -req <name of json request file located in driver/requests>
     */
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SystemCreateSchedulerProfile has failed");
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.reqFilename == null)
    		throw new Exception("reqFilename is null and is required for SystemCreateSchedulerProfile operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemCreateSchedulerProfile operation, THROWING ERROR");
    	
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
        ReqCreateSchedulerProfile schedReq = TapisGsonUtils.getGson().fromJson(reqString, ReqCreateSchedulerProfile.class);
        
        //----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE AND USE CLIENT OBJECT -----------------------//
        // Create the scheduler profile
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        sysClient.createSchedulerProfile(schedReq);
        System.out.println("Finished processing " + req.toString() + ".");
	}
}
