package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.files.client.FilesClient;

public class FileDelete 
{
	/** Deletes a Tapis file 
	 *
    *  FileDelete -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -system <name of tapis system> -path <full path to file to be operated on>
    */
	public static void main(String[] args) throws Exception
	{
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for FileMkDir has failed");
        }

    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for FileMkDir operation, THROWING ERROR");
    	
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for FileMkDir operation, THROWING ERROR");

    	if(parms.pathName == null)
    		throw new Exception("pathName is null and is required for FileMkDir operation, THROWING ERROR");
    	
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        // Create the app.
        var filesClient = new FilesClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        var fileResp = filesClient.delete(parms.systemName, parms.pathName);
        System.out.println(fileResp.toString());
	}
}
