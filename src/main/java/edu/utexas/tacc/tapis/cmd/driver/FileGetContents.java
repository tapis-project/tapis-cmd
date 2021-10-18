package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.files.client.FilesClient;

public class FileGetContents 
{
	/** Gets the file contents 
     * 
     * FileGetContents -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -system <name of tapis system> -path <full path to file to be operated on>
     * -zip <boolean flag for zipping file contents being returned>
     */
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for FileGetContents has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for FileGetContents operation, THROWING ERROR");
    	
    	if(parms.pathName == null)
    		throw new Exception("pathName is null and is required for FileGetContents operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for FileGetContents operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Check file contents
        var filesClient = new FilesClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	TestUtils.setOboHeaders(filesClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        var fileContents = filesClient.getFileContents(parms.systemName, parms.pathName, parms.zipFlag);
        System.out.println("File Name: " + fileContents.getName());
        System.out.println("Class Name: " + fileContents.getClass()); 
        filesClient.close();
	}
}
