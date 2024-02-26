package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import com.google.gson.JsonObject;

import edu.utexas.tacc.tapis.client.shared.exceptions.TapisClientException;
import edu.utexas.tacc.tapis.security.client.SKClient;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;

/** This test class exercises SK's has_children optimization.  The has_children flag
 * in the sk_roles table avoids issuing unnecessary recursive queries when processing
 * a user's roles or permissions.
 * 
 * There are two ways to use this test.  The first is to run the test and let it
 * validate the correctness of the results.  Specifically, the test will verify that 
 * a user has the expected roles and permissions when various assignments are in 
 * place.
 * 
 * The second is to run this test in debug mode with breakpoints at various places so
 * that the database and SK log can be inspected to verify that the optimizations 
 * actually avoid unnecessary recursive queries.
 * 
 * @author rcardone
 */
public class SKHasChildrenTest 
{
	// Total number of rows in this test.  Rows are numbered 1 to this total.
	// The total is used to during checking to make sure the expected number
	// of elements are returned.
	private final static int TOTAL_ROWS = 4;
	
	// Client shared by all methods.
	CMDUtilsParameters _parms;
	private SKClient   _skClient;
	private boolean    _deleteRoles;
	
	/** Create a test instance and execute it.
	 * 
	 * @param args command line arguments
	 * @throws Exception
	 */
    public static void main(String[] args) throws Exception 
    {
    	var test = new SKHasChildrenTest();
    	test.execute(args);
    }
    
    /** Main execution method. 
     */
    private void execute(String[] args) throws Exception
    {
        //----------------------- INITIALIZE PARMS -----------------------//
        try {_parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization has failed with Exception: ",e);
        }
        
        //----------------------- VALIDATE PARMS -----------------------//
        if (_parms.reqFilename == null)
            throw new Exception("reqFilename is null and is required for appCreate, THROWING ERROR");
        
        if (_parms.jwtFilename == null)
            throw new Exception("jwtFilename is null and is required for appCreate, THROWING ERROR");
        
        if (_parms.tenant == null)
        	throw new Exception("The tenant command line parameter must be specified, THROWING ERROR");
        
        System.out.println("Processing " + _parms.reqFilename);
        
        //----------------------- READ JSON REQUEST INTO REQ OBJECT -----------------------//
        // Convert json string into an app create request.
        var req = TapisGsonUtils.getGson().fromJson(DriverUtils.readRequestFile(_parms.reqFilename), JsonObject.class);
    	
        // Set instance fields.
    	_deleteRoles = req.get("deleteRoles").getAsBoolean();
        
        //----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(_parms.jwtFilename, true);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        _skClient = new SKClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(_parms.oboTenant != null)
            DriverUtils.setOboHeaders(_skClient, _parms.oboUser, _parms.oboTenant);
        
        //----------------------- INTEGRATION TEST ------------------------//
        // Validate that has_children works as intended.
        testHasChildren();
    }
    
    /** Test whether the has_children flag works properly. 
     * @throws TapisClientException */
    private void testHasChildren() 
     throws Exception
    {
    	// The calls below assume rows 1 though TOTAL_ROWS are fully populated.
    	
    	// Create role and permissions and add to parent.
    	initRolesAndPerms(1, 'a', "xy", 0);
    	initRolesAndPerms(1, 'b', "xy", 0);
    	initRolesAndPerms(2, 'a', "xy", 1);
    	initRolesAndPerms(2, 'b', "xy", 1);
    	initRolesAndPerms(3, 'a', "xy", 2);
    	initRolesAndPerms(3, 'b', "xy", 2);
    	initRolesAndPerms(4, 'a', "xy", 3);
    	initRolesAndPerms(4, 'b', "xy", 3);
    	
    	// Check what we just created.
    	initRolesAndPermsCheck(1, 'a', "xy", 0);
    	initRolesAndPermsCheck(1, 'b', "xy", 0);
    	initRolesAndPermsCheck(2, 'a', "xy", 1);
    	initRolesAndPermsCheck(2, 'b', "xy", 1);
    	initRolesAndPermsCheck(3, 'a', "xy", 2);
    	initRolesAndPermsCheck(3, 'b', "xy", 2);
    	initRolesAndPermsCheck(4, 'a', "xy", 3);
    	initRolesAndPermsCheck(4, 'b', "xy", 3);
    	
    	
    	
    	System.out.println("SKHasChildrenTest successfully completed.");
    }
    
    /** Create a role, add its permissions and, optionally, add it as a
     * child to the another role.  Roles can be thought of as arranged
     * in a tree with rows and colums that indicate the parent-child and
     * sibling relationships, respecitively.  
     * 
     * Roles are added to numbered rows starting at 1. Roles in the same
     * row are siblings and distinguished with an alphabetic name: a, b, c,
     * etc.  Each sibling can be thought of as residing in a column.  An 
     * example row name would be R1a, R1b, R2a, etc.
     * 
     * Permissions use the row number and name of the role to which they
     * will be added.  Each character in the permNames string is used to 
     * generate a full permission name. Example permission names are P1a-x, 
     * P1a-y, P2a-x, etc. 
     * 
     * Finally, roles are added as children to in the role in the specified
     * role in the same column.  A zero parentRow indicates that the role
     * has no parent.  For ease of testing, roles are usually added as 
     * children to only to roles in the previous row.
     * 
     * @param row the row number of the role (1 or more)
     * @param rowName the unique name of the role in a row
     * @param permNames the permission names, each character is used in a permission name
     * @param parentRow the role's parent, zero means no parent.
     * @throws TapisClientException 
     */
    private void initRolesAndPerms(int row, char rowName, String permNames, int parentRow) 
     throws TapisClientException
    {
    	// Create role name.
    	String roleName = "R" + row + rowName;
    	
    	// Create the role's permission names.
    	String[] perms = new String[permNames.length()];
    	for (int i = 0; i < permNames.length(); i++)
    		perms[i] = "P" + row + rowName + '-' + permNames.charAt(i);
    	
    	// Create the role.
    	_skClient.createRole(_parms.tenant, roleName, "test role");
    	
    	// Add permissions to the role.
    	for (var permName : perms) 
    		 _skClient.addRolePermission(_parms.tenant, roleName, permName);
    	 
    	// Conditionally add to parent role.
        if (parentRow > 0) {
        	String parentRole = "R" + parentRow + rowName;
   		 	_skClient.addChildRole(_parms.tenant, parentRole, roleName);
        }
    }
    
    /** Check some of the initial relationships.
     */
    private void initRolesAndPermsCheck(int row, char rowName, String permNames, int parentRow)
     throws TapisClientException
    {
    	// Create role name.
    	String roleName = "R" + row + rowName;
    	
    	// Create the role's permission names.
    	String[] perms = new String[permNames.length()];
    	for (int i = 0; i < permNames.length(); i++)
    		perms[i] = "P" + row + rowName + '-' + permNames.charAt(i);
    	
    	// Retrieve role.
    	_skClient.getRoleByName(_parms.tenant, roleName);
    	
    	// Retrieve the role's immediate permissions.
    	boolean immediate = true;
    	var immediatePerms = _skClient.getRolePermissions(_parms.tenant, roleName, immediate);
    	
    	// Check the role's immediate permissions.
    	for (int i = 0; i < permNames.length(); i++)
    		if (!immediatePerms.contains(perms[i]))
    			throw new TapisClientException("Expected role " + roleName + " to contain " +
    		                                   "an immediate permission named " + perms[i] + ".");
    	
    	// Check that all the permissions are returned.
    	immediate = false;
        var allPerms = _skClient.getRolePermissions(_parms.tenant, roleName, immediate);
        int expectedPermCount = (TOTAL_ROWS - row + 1) * 2; // perms per role based on role row.
        if (allPerms.size() != expectedPermCount)
        	throw new TapisClientException("Expected role " + roleName + " to contain " +
                             expectedPermCount + " permission but found " + allPerms.size() + ".");
    }
}
