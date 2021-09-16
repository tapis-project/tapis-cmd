package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.files.client.FilesClient;

//TODO: NEED TO RUN TESTS FOR FILES

public class FileMkDir 
{
	/** Makes a directory 
     * 
     * FileMkDir -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -system <name of tapis system> -path <full path to file to be operated on>
     */
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for FileMkDir has failed");
        }

    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for FileMkDir operation, THROWING ERROR");
    	
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for FileMkDir operation, THROWING ERROR");

    	if(parms.pathName == null)
    		throw new Exception("pathName is null and is required for FileMkDir operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
    	
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create the app.
        var filesClient = new FilesClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	TestUtils.setOboHeaders(filesClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        var fileResp = filesClient.mkdir(parms.systemName, parms.pathName);
        System.out.println(fileResp.toString());
	}
}
