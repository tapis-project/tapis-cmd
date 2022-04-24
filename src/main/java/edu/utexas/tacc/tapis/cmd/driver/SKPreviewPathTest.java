package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.security.client.SKClient;

/** This test class exercises SK's previewPathPrefix API to illustrate how it works
 * and what the expected results should look like.
 * 
 * @author rcardone
 */
public class SKPreviewPathTest 
{
    private static final String ROLE_NAME = "SKPreviewPathTestRole";
    
    public static void main(String[] args) throws Exception 
    {
        //----------------------- INITIALIZE PARMS -----------------------//
        CMDUtilsParameters parms = null;
        try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for SKPreviewPathTest has failed with Exception: ",e);
        }
        
        //----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename, true);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        var skClient = new SKClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
            DriverUtils.setOboHeaders(skClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        // Create role
        var tenant = parms.oboTenant;
        var url = skClient.createRole(tenant, ROLE_NAME, "Test role for SKPreviewPathTest");
        
        // Retieve role
        var role = skClient.getRoleByName(tenant, ROLE_NAME);
        System.out.println(role.toString());
        
        // Assign permission to role.
        skClient.addRolePermission(tenant, ROLE_NAME, "files:dev:read:mysystem:/dir1/dir2/dir3/f.txt");
        skClient.addRolePermission(tenant, ROLE_NAME, "files:dev:read:mysystem:dir1/dir2/dir3/g.txt");
        
        // Check the perms.
        var perms = skClient.getRolePermissions(tenant, ROLE_NAME, false);
        System.out.println("Number of permissions is role " + ROLE_NAME + ": " + perms.size());
        for (var p : perms) System.out.println("  " + p);
        
        // --- Preview changes w/trailing slashes ---
        // ------------------------------------------
        String oldPath = "/dir1/";
        String newPath = "/dir66/";
        var changes = skClient.previewPathPrefix(tenant, "files", ROLE_NAME, "mysystem", "mysystem", "/dir1/", "/dir66/");
        System.out.println("\n---\nNumber of transformations (old=" + oldPath + ", new=" + newPath + "): " + changes.size());
        for (var c : changes) System.out.println(c.toString());
        
        // Preview changes.
        oldPath = "dir1/";
        newPath = "dir66/";
        changes = skClient.previewPathPrefix(tenant, "files", ROLE_NAME, "mysystem", "mysystem", "/dir1/", "/dir66/");
        System.out.println("\n---\nNumber of transformations (old=" + oldPath + ", new=" + newPath + "): " + changes.size());
        for (var c : changes) System.out.println(c.toString());
        
        // Preview changes.
        oldPath = "/dir1/";
        newPath = "dir66/";
        changes = skClient.previewPathPrefix(tenant, "files", ROLE_NAME, "mysystem", "mysystem", "/dir1/", "/dir66/");
        System.out.println("\n---\nNumber of transformations (old=" + oldPath + ", new=" + newPath + "): " + changes.size());
        for (var c : changes) System.out.println(c.toString());
        
        // Preview changes.
        oldPath = "dir1/";
        newPath = "/dir66/";
        changes = skClient.previewPathPrefix(tenant, "files", ROLE_NAME, "mysystem", "mysystem", "/dir1/", "/dir66/");
        System.out.println("\n---\nNumber of transformations (old=" + oldPath + ", new=" + newPath + "): " + changes.size());
        for (var c : changes) System.out.println(c.toString());
                
        // --- Preview changes w/o trailing slashes ---
        // ----------------------------------------------
        oldPath = "/dir1";
        newPath = "/dir66";
        changes = skClient.previewPathPrefix(tenant, "files", ROLE_NAME, "mysystem", "mysystem", "/dir1/", "/dir66/");
        System.out.println("\n---\nNumber of transformations (old=" + oldPath + ", new=" + newPath + "): " + changes.size());
        for (var c : changes) System.out.println(c.toString());
        
        // Preview changes.
        oldPath = "dir1";
        newPath = "dir66";
        changes = skClient.previewPathPrefix(tenant, "files", ROLE_NAME, "mysystem", "mysystem", "/dir1/", "/dir66/");
        System.out.println("\n---\nNumber of transformations (old=" + oldPath + ", new=" + newPath + "): " + changes.size());
        for (var c : changes) System.out.println(c.toString());
        
        // Preview changes.
        oldPath = "/dir1";
        newPath = "dir66";
        changes = skClient.previewPathPrefix(tenant, "files", ROLE_NAME, "mysystem", "mysystem", "/dir1/", "/dir66/");
        System.out.println("\n---\nNumber of transformations (old=" + oldPath + ", new=" + newPath + "): " + changes.size());
        for (var c : changes) System.out.println(c.toString());
        
        // Preview changes.
        oldPath = "dir1";
        newPath = "/dir66";
        changes = skClient.previewPathPrefix(tenant, "files", ROLE_NAME, "mysystem", "mysystem", "/dir1/", "/dir66/");
        System.out.println("\n---\nNumber of transformations (old=" + oldPath + ", new=" + newPath + "): " + changes.size());
        for (var c : changes) System.out.println(c.toString());
                
        // Clean up.
        int deleted = skClient.deleteRoleByName(tenant, ROLE_NAME);
        System.out.println("\nRoles deleted: " + deleted);
    }

}
