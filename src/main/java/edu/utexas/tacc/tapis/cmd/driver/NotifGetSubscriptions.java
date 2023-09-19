package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.notifications.client.NotificationsClient;

public class NotifGetSubscriptions
{
	/** Get systems
     *
     * TODO: Add option for listType
     * SystemGet -jwt <jwt filename located in $HOME/Tapis-cmd/jwt>
     */
	public static void main(String[] args) throws Exception
    {
      String className = NotifGetSubscriptions.class.getSimpleName();
		//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception(String.format("Parms initialization for %s has failed with Exception: %s", className ,e));
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for SystemGet operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create the app.
        var notifClient = new NotificationsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	DriverUtils.setOboHeaders(notifClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        String subOwner = "jobs"; // change this to search for different subscription owners
        var notifList = notifClient.getSubscriptions(subOwner);
        System.out.println("Total number of notifications: " + notifList.size() + "\n");
        for (var sub : notifList) System.out.println(sub.toString() + "\n");
    }
}
