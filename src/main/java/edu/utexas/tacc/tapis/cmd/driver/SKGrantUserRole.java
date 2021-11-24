package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.security.client.SKClient;
import edu.utexas.tacc.tapis.security.client.gen.model.ReqGrantUserRole;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;

/** This test class exercises SK's previewPathPrefix API to illustrate how it works
 * and what the expected results should look like.
 * 
 * @author rcardone
 */
public class SKGrantUserRole 
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
        var req = TapisGsonUtils.getGson().fromJson(TestUtils.readRequestFile(parms.reqFilename), ReqGrantUserRole.class);
        
        //----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        var skClient = new SKClient(props.getProperty("BASE_URL")+"/v3", props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
            TestUtils.setOboHeaders(skClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        // Create role
        var updates = skClient.grantUserRole(req.getTenant(), req.getUser(), req.getRoleName());
        System.out.println("Number of newly assigned roles: " + updates);
    }
}
