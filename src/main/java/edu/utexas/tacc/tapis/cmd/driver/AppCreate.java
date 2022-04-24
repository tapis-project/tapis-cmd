package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.apps.client.AppsClient;
import edu.utexas.tacc.tapis.apps.client.gen.model.ReqPostApp;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;

public class AppCreate 
{
    /** Creates a Tapis app from a request file
     * 
     * AppCreate -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -req <name of json request file located in driver/requests>
     */
    public static void main(String[] args) throws Exception
    {
    	//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for appCreate has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.reqFilename == null)
    		throw new Exception("reqFilename is null and is required for appCreate, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for appCreate, THROWING ERROR");
        
    	System.out.println("Processing " + parms.reqFilename);
    	
        //----------------------- READ JSON REQUEST INTO REQ OBJECT -----------------------//
        // Convert json string into an app create request.
        ReqPostApp appReq = TapisGsonUtils.getGson().fromJson(DriverUtils.readRequestFile(parms.reqFilename), ReqPostApp.class);
        
        //----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create the app.
        var appsClient = new AppsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	DriverUtils.setOboHeaders(appsClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        appsClient.createApp(appReq);
        System.out.println("Finished processing " + parms.reqFilename);
    }
}
