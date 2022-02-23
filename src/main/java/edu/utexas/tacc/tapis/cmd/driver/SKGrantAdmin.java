package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.security.client.SKClient;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqGrantAdminRole;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;

/** This test class exercises SK's previewPathPrefix API to illustrate how it works
 * and what the expected results should look like.
 * 
 * @author rcardone
 */
public class SKGrantAdmin 
{
    public static void main(String[] args) throws Exception 
    {
        //----------------------- INITIALIZE PARMS -----------------------//
        CMDUtilsParameters parms = null;
        try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization has failed with Exception: ",e);
        }
        
        //----------------------- VALIDATE PARMS -----------------------//
        if(parms.reqFilename == null)
            throw new Exception("reqFilename is null and is required for appCreate, THROWING ERROR");
        
        if(parms.jwtFilename == null)
            throw new Exception("jwtFilename is null and is required for appCreate, THROWING ERROR");
        
        System.out.println("Processing " + parms.reqFilename);
        
        //----------------------- READ JSON REQUEST INTO REQ OBJECT -----------------------//
        // Convert json string into an app create request.
        var req = TapisGsonUtils.getGson().fromJson(DriverUtils.readRequestFile(parms.reqFilename), ReqGrantAdminRole.class);
        
        //----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename, true);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        var skClient = new SKClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- USE CLIENT OBJECT -----------------------//
        // Create role
        var updates = skClient.grantAdminRole(req.getTenant(), req.getUser());
        System.out.println("Number of newly assigned roles: " + updates);
    }

}
