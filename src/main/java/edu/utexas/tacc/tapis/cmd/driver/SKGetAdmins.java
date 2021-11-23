package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.security.client.SKClient;

/** This test class exercises SK's previewPathPrefix API to illustrate how it works
 * and what the expected results should look like.
 * 
 * @author rcardone
 */
public class SKGetAdmins 
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
        if(parms.jwtFilename == null)
            throw new Exception("jwtFilename is null and is required for appCreate, THROWING ERROR");
        
        //----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        var skClient = new SKClient(props.getProperty("BASE_URL")+"/v3", props.getProperty("USER_JWT"));
  
        //----------------------- USE CLIENT OBJECT -----------------------//
        // Create role
        var admins = skClient.getAdmins(parms.tenant);
        System.out.println(admins);
    }

}