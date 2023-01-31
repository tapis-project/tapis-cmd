package edu.utexas.tacc.tapis.cmd.driver;

import edu.utexas.tacc.tapis.files.client.FilesClient;

import java.util.Properties;

public class FileNativeLinuxGetFacl
{
	/** Get file ACLs for a file or a directory
     * 
     * NativeLinuxGetFacl -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -system <name of tapis system> -path <full path to file to be operated on>
     */
    public static void main(String[] args) throws Exception
    {
    	//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for NativeLinuxGetFacl has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for NativeLinuxGetFacl operation, THROWING ERROR");
    	
    	if(parms.pathName == null)
    		throw new Exception("pathName is null and is required for NativeLinuxGetFacl operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for NativeLinuxGetFacl operation, THROWING ERROR");
    	
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
        var response = filesClient.nativeLinuxGetFacl(parms.systemName, parms.pathName);
        if (response == null) {
            System.out.println("Null list returned!");
        } else {
            var list = response.getResult();
            System.out.println("Number of files returned: " + list.size());
            for (var aclInfo : list) {
                StringBuilder sb = new StringBuilder();
                if(aclInfo.getDefaultAcl()) {
                    sb.append("default:");
                }
                if(aclInfo.getType() != null) {
                    sb.append(aclInfo.getType());
                }
                sb.append(":");
                if(aclInfo.getPrincipal() != null) {
                    sb.append(aclInfo.getPrincipal());
                }
                sb.append(":");
                if(aclInfo.getPermissions() != null) {
                    sb.append(aclInfo.getPermissions());
                }
                System.out.println(sb);
            }
            System.out.println("OK");
        }
    }  
}
