package edu.utexas.tacc.tapis.cmd.driver;

import java.util.ArrayList;
import java.util.List;
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
 * actually avoid unnecessary recursive queries.  (Temporary log messages might work
 * better to avoid timeouts.)
 * 
 * @author rcardone
 */
final public class SKHasChildrenTest 
{
    /* ********************************************************************** */
    /*                              Constants                                 */
    /* ********************************************************************** */
	// Total number of rows in this test.  Rows are numbered 1 to this total.
	// The total is used to during checking to make sure the expected number
	// of elements are returned.
	private final static int    TOTAL_ROWS = 4;      // depth of each role tree
	private final static String COLUMN_NAMES = "ab"; // length implies num of role trees
	private final static String PERM_NAMES   = "xy"; // length implies perms per role
	
    /* ********************************************************************** */
    /*                                Fields                                  */
    /* ********************************************************************** */
	// Client shared by all methods.
	private CMDUtilsParameters _parms;
	private SKClient       _skClient;
	private Boolean        _deleteRoles;
	private List<TestName> _names;
	
    /* ********************************************************************** */
    /*                            Public Methods                              */
    /* ********************************************************************** */
    /* ---------------------------------------------------------------------- */
    /* main:                                                                  */
    /* ---------------------------------------------------------------------- */
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
    
    /* ********************************************************************** */
    /*                           Private Methods                              */
    /* ********************************************************************** */
    /* ---------------------------------------------------------------------- */
    /* execute:                                                               */
    /* ---------------------------------------------------------------------- */
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
    	if (_deleteRoles == null) _deleteRoles = Boolean.TRUE; // the default
        
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
    
    /* ---------------------------------------------------------------------- */
    /* testHasChildren:                                                       */
    /* ---------------------------------------------------------------------- */
    /** Test whether the has_children flag works properly. 
     * @throws TapisClientException */
    private void testHasChildren() 
     throws Exception
    {
    	// The calls below assume rows 1 though TOTAL_ROWS are fully populated.
    	
    	// Create all role and permission names.
    	initNames();
    	
    	// Create roles and permissions and parent/child relationships.
    	initRolesAndPerms();
    	
    	// Check what we just created.
    	initRolesAndPermsCheck();
    	
    	// Check that the user has all expected roles.
    	checkUserRoles();
    	
    	// Remove child tests.
    	removeChildRole();
    	
    	// Clean up all changes to the database.
    	if (_deleteRoles) deleteRoles();
    	
    	System.out.println("SKHasChildrenTest successfully completed.");
    }
    
    /* ---------------------------------------------------------------------- */
    /* initNames:                                                             */
    /* ---------------------------------------------------------------------- */
    /** Create a list of all role and permission names that will be added to the
     * database.  The naming conventions are:
     * 
     * 	- Roles:  "R" + rowNum + columnChar
     *  - Permissions: "P" + rowNum + columnChar + "-" + permChar
     * 
     * Only one number appears in role names and this convention is used by 
     * getParentRoleName() to derive a parent role name from a child's role name.
     * 
     * The _names field elements begin with row 1/column 1, then row 1/column 2, 
     * row 2/column 1, and so on.  Here's what the first two elements (row 1/column 1
     * and row 1/column 2) in the _names lists look like:
     * 
     *  	roleName: "R1a"
     *  	permNames: ["R1a-x", "R1a-y"]
     *  	row: 1
     *  
     *  	roleName: "R1b"
     *  	permNames: ["R1b-x", "R1b-y"]
     *  	row: 1
     */
    private void initNames()
    {
    	// Get the number of columns and perms from their name lengths.
    	final int numColumns = COLUMN_NAMES.length();
    	final int numPerms   = PERM_NAMES.length();
    	
    	// Initialize the names array.
    	_names = new ArrayList<TestName>(TOTAL_ROWS * numColumns); 
    	
    	for (int i = 0; i < TOTAL_ROWS; i++)
    	{
    		final int row = i + 1;
    		for (int j = 0; j < numColumns; j++)
    		{
    			var name = new TestName();
    			name.row = row;
    			name.roleName = "R" + row + COLUMN_NAMES.charAt(j);
    			name.permNames = new String[numColumns];
    	    	for (int k = 0; k < numPerms; k++)
    	    		name.permNames[k] = "P" + row + COLUMN_NAMES.charAt(j) + 
    	    		                    '-' + PERM_NAMES.charAt(k);
    	    	
    	    	// Save the name.
    	    	_names.add(name);
    	    	System.out.println("Adding name: " + name);
    		}
    	}
    }

    /* ---------------------------------------------------------------------- */
    /* getParentRoleName:                                                     */
    /* ---------------------------------------------------------------------- */
    private String getParentRoleName(TestName name)
    {
    	// This code relies on the role naming conventions established by
    	// initNames in which the row number is the only number in the 
    	// role name.  This method should not be called for row = 1.
		String curRow  = Integer.toString(name.row);
		String prevRow = Integer.toString(name.row - 1);
		return name.roleName.replace(curRow, prevRow);  // should only replace 1 char
    }
    
    /* ---------------------------------------------------------------------- */
    /* initRolesAndPerms:                                                     */
    /* ---------------------------------------------------------------------- */
    /** Create a role, add its permissions and, optionally, add it as a
     * child to the another role.  Roles can be thought of as arranged
     * in a tree with rows and columns that indicate the parent-child and
     * sibling relationships, respectively.  
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
     * @throws TapisClientException 
     */
    private void initRolesAndPerms() 
     throws TapisClientException
    {
    	// Create and populate each role.  All but the last role gets 
    	// assigned to a parent role--the previous role that was processed.
    	for (var name : _names) {
        	// Create the role.
        	_skClient.createRole(_parms.tenant, name.roleName, "test role");
    		
        	// Add permissions to the role.
        	for (var permName : name.permNames) 
        		 _skClient.addRolePermission(_parms.tenant, name.roleName, permName);

        	// Conditionally add to parent role. Note that this code relies on
        	// the fact that roles are processed in row order, from lowest to 
        	// highest.  This ordering is implemented by initNames(). 
        	if (name.row > 1) {
        		String parentName = getParentRoleName(name);
     		 	_skClient.addChildRole(_parms.tenant, parentName, name.roleName);
        	}
    	}
    }
    
    /* ---------------------------------------------------------------------- */
    /* initRolesAndPermsCheck:                                                */
    /* ---------------------------------------------------------------------- */
    /** Check some of the initial relationships:
     * 
     * 	1. That each role's immediate permission are the expected ones.
     *  2. That the number of permissions available in a role is the expected 
     *     sum of immediate and transitive permissions.
     */
    private void initRolesAndPermsCheck()
     throws TapisClientException
    {
    	// Check each role that was created.
    	for (var name : _names) {
    		// Retrieve role.
    	    _skClient.getRoleByName(_parms.tenant, name.roleName);
    	
    	    // Retrieve the role's immediate permissions.
    	    boolean immediate = true;
    	    var immediatePerms = _skClient.getRolePermissions(_parms.tenant, name.roleName, immediate);
    	
    	    // Check the role's immediate permissions.
    	    for (int i = 0; i < name.permNames.length; i++)
    	    	if (!immediatePerms.contains(name.permNames[i]))
    	    		throw new TapisClientException("Expected role " + name.roleName + " to contain " +
    		                                   "	an immediate permission named " + name.permNames[i] + ".");
    	
    	    // Retrieve all permissions available in this role.
    	    immediate = false;
    	    var allPerms = _skClient.getRolePermissions(_parms.tenant, name.roleName, immediate);
    	    
    	    // Check that all the permissions are returned.
    	    int expectedPermCount = (TOTAL_ROWS - name.row + 1) * 2; // perms per role based on role row.
    	    if (allPerms.size() != expectedPermCount)
    	    	throw new TapisClientException("Expected role " + name.roleName + " to contain " +
                             	expectedPermCount + " permission but found " + allPerms.size() + ".");
    	}
    }
    
    /* ---------------------------------------------------------------------- */
    /* checkUserRoles:                                                        */
    /* ---------------------------------------------------------------------- */
    /** This method grants a role with children to a fake (and unused) user, 
     * checks that the expected number of roles are found when the user's roles
     * are retrieved, and then revokes the user's grant.  The same steps are then
     * performed for a role with no children.
     * 
     * @throws TapisClientException
     */
    private void checkUserRoles() throws TapisClientException
    {
    	// Pick a user that doesn't exist so that we can 
    	// get precise role counts when we query the user's roles.
    	final String user = "bozo";
    	
    	// Assign a root role that has 3 children.
    	int grants = _skClient.grantUserRole(_parms.tenant, user, _names.get(0).roleName);
    	var roles = _skClient.getUserRoles(_parms.tenant, user);
    	if (roles.size() != 4) {
	    	throw new TapisClientException("After granting user " + user + " role " +
	    			_names.get(0).roleName + " we expected the user to have 4 roles assigned " + 
	    			"but found " + roles.size() + " roles instead.");
    	}
    	_skClient.revokeUserRole(_parms.tenant, user, _names.get(0).roleName);

    	// Assign a leaf role that has no children.
    	grants = _skClient.grantUserRole(_parms.tenant, user, _names.get(7).roleName);
    	roles = _skClient.getUserRoles(_parms.tenant, user);
    	if (roles.size() != 1) {
	    	throw new TapisClientException("After granting user " + user + " role " +
	    			_names.get(7).roleName + " we expected the user to have 1 role assigned " + 
	    			"but found " + roles.size() + " roles instead.");
    	}
    	_skClient.revokeUserRole(_parms.tenant, user, _names.get(7).roleName);
    }
    
    /* ---------------------------------------------------------------------- */
    /* removeChildRole:                                                       */
    /* ---------------------------------------------------------------------- */
    private void removeChildRole() throws TapisClientException
    {
    	// Get the number of permission of the row 1 and 3 roles.
	    // Retrieve all permissions available in this role.
	    final boolean immediate = false;
	    var row1Perms = _skClient.getRolePermissions(_parms.tenant, _names.get(1).roleName, immediate);
	    var row5Perms = _skClient.getRolePermissions(_parms.tenant, _names.get(5).roleName, immediate);
	    
	    // Remove one child.
	    _skClient.removeChildRole(_parms.tenant, _names.get(5).roleName, _names.get(7).roleName);
	    var row1Perms2 = _skClient.getRolePermissions(_parms.tenant, _names.get(1).roleName, immediate);
	    var row5Perms2 = _skClient.getRolePermissions(_parms.tenant, _names.get(5).roleName, immediate);
	    
	    // Check the row 1 role's perm count..
	    if (row1Perms.size() - row1Perms2.size() != 2) {
	    	throw new TapisClientException("Removing role " + _names.get(7).roleName + " from role " +
	    			_names.get(5).roleName + " did not lead to expected number (2) less permissions in " + 
	    			_names.get(1).roleName + ".");
	    }
	    
	    // Check the row 3 role's perm count.
	    if (row5Perms.size() - row5Perms2.size() != 2) {
	    	throw new TapisClientException("Removing role " + _names.get(7).roleName + " from role " +
	    			_names.get(5).roleName + " did not lead to expected number (2) less permissions in " + 
	    			_names.get(5).roleName + ".");
	    }
	    
    }
    
    /* ---------------------------------------------------------------------- */
    /* deleteRoles:                                                           */
    /* ---------------------------------------------------------------------- */
    private void deleteRoles() throws TapisClientException
    {
    	// Delete each role that was created.
    	for (var name : _names) {
    		_skClient.deleteRoleByName(_parms.tenant, name.roleName);
    	}
    	
    	System.out.println("All " + _names.size() + " test roles have been deleted.");
    }

    /* ********************************************************************** */
    /*                            TestName Class                              */
    /* ********************************************************************** */
    /** Container for role and permission name information. */
    private static class TestName
    {
    	private int      row;
    	private String   roleName;
    	private String[] permNames;
    	
    	@Override
    	public String toString() {
    		String s =  "row: " + row + ", roleName: " + roleName + ", permNames: [";
    		for (int i = 0; i < permNames.length; i++ ) 
    			if (i == 0) s += permNames[i];
    			else s += ", " + permNames[i];
    		return s + "]";
    	}
    }
}
