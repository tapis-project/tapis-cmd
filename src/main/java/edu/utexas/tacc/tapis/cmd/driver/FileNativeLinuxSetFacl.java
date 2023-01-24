package edu.utexas.tacc.tapis.cmd.driver;

import edu.utexas.tacc.tapis.files.client.FilesClient;
import edu.utexas.tacc.tapis.files.client.gen.model.NativeLinuxSetFaclRequest;

import java.util.Properties;

public class FileNativeLinuxSetFacl
{
	/** Get file ACLs for a file or a directory
     * 
     * NativeLinuxGetFacl -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -system <name of tapis system> -path <full path to file to be operated on>
     */
    public static void main(String[] args) throws Exception
    {
        NativeLinuxSetFaclRequest.RecursionMethodEnum recursion = NativeLinuxSetFaclRequest.RecursionMethodEnum.NONE;
        NativeLinuxSetFaclRequest.OperationEnum operation = null;

    	//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for NativeLinuxSetFacl has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for NativeLinuxSetFacl operation, THROWING ERROR");
    	
    	if(parms.pathName == null)
    		throw new Exception("pathName is null and is required for NativeLinuxSetFacl operation, THROWING ERROR");

        if(parms.operation == null) {
            throw new Exception("operation is null and is required for NativeLinuxGetSacl operation, THROWING ERROR");
        } else {
            operation = NativeLinuxSetFaclRequest.OperationEnum.fromValue(parms.operation);
        }

        if((parms.aclString == null) && (!operation.equals(NativeLinuxSetFaclRequest.OperationEnum.REMOVE_ALL))
                && (!operation.equals(NativeLinuxSetFaclRequest.OperationEnum.REMOVE_DEFAULT))) {
            throw new Exception("aclString is null and is required for this NativeLinuxGetSacl operation, THROWING ERROR");
        }

        if(parms.recursion == null) {
            recursion = NativeLinuxSetFaclRequest.RecursionMethodEnum.NONE;
        } else {
            recursion = NativeLinuxSetFaclRequest.RecursionMethodEnum.fromValue(parms.recursion);
        }

    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for NativeLinuxSetFacl operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename);
    	
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create the app.
        var filesClient = new FilesClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	DriverUtils.setOboHeaders(filesClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        var response = filesClient.nativeLinuxSetFacl(parms.systemName, parms.pathName,
                operation, recursion, parms.aclString);
        if (response == null) {
            System.out.println("Null list returned!");
        } else {
            var result = response.getResult();
            System.out.println("Command: " + result.getCommand());
            System.out.println("Exit Code: " + result.getExitCode());
            System.out.println("stdout: " + result.getStdOut());
            System.out.println("stderr: " + result.getStdErr());
            System.out.println("OK");
        }
    }  
}
