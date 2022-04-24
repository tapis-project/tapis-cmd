package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.files.client.FilesClient;
import edu.utexas.tacc.tapis.shared.utils.FilesListSubtree;

public class FilesSubtreeList 
{
	/** Lists file subtree
     * 
     * FilesSubtreeList -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -system <name of tapis system> -path <full path to file to be operated on>
     */
	public static void main(String[] args) throws Exception
    {
		//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for FilesSubtreeList has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for FilesSubtreeList operation, THROWING ERROR");
    	
    	if(parms.pathName == null)
    		throw new Exception("pathName is null and is required for FilesSubtreeList operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for FilesSubtreeList operation, THROWING ERROR");
    	
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
        var filesListSubtree = new FilesListSubtree(filesClient, parms.systemName, parms.pathName);
        var list = filesListSubtree.list();
        if (list == null) {
            System.out.println("Null list returned!");
        } else {
            System.out.println("Number of files returned: " + list.size());
            for (var f : list) {
                System.out.println("\nfile:  " + f.getName());
                System.out.println("  size:  " + f.getSize());
                System.out.println("  time:  " + f.getLastModified());
                System.out.println("  path:  " + f.getPath());
                System.out.println("  type:  " + f.getType());
                System.out.println("  owner: " + f.getOwner());
                System.out.println("  group: " + f.getGroup());
                System.out.println("  perms: " + f.getNativePermissions());
                System.out.println("  mime:  " + f.getMimeType());
            }
        }
    }
}
