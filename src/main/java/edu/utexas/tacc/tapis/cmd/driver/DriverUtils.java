package edu.utexas.tacc.tapis.cmd.driver;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import edu.utexas.tacc.tapis.client.shared.ITapisClient;

public class DriverUtils 
{
    public static final String TAPIS_CMD_JWT_PROFILE_DIR = "TapisCmd/jwt";
    public static final String TAPIS_CMD_REQUEST_HOME_DIR = "TapisCmd/requests";
    public static final String REQUEST_SUBDIR = "requests";
    
    // Profile file name extension.
    public static final String PROFILE_EXT = ".properties";
    public static final String PEM_EXT = ".pem";
    
    /** Convenience method used by service tests that automatically insert
     * the v3 into the base url path.
     * 
     * @param profileName the file name without extension
     * @return the properties object populated from file
     * @throws IOException on error
     */
    public static Properties getTestProfile(String profileName) throws IOException
    {
        return getTestProfile(profileName, false);
    }
    
    /** Load the contents of the properties file into memory.  The property file
     * is expected to contain two key/value pairs:
     * 
     *      BASE_URL=<the base url of the jobs service>
     *      USER_JWT=<the requestor's JWT>
     * 
     * @param profileName the file name without extension
     * @param addV3ToBaseUrl
     * @return the properties object populated from file
     * @throws IOException on error
     */
    public static Properties getTestProfile(String profileName, boolean addV3ToBaseUrl) 
     throws IOException
    {
        // Create the path to the profile and read the file into a string.
    	// The path is created with the users current directory so that it does not depend on the variability
    	// of a users default tapis project directory. Rather it navigates backwards so that the prefix can be as dynamic as possible.
    	
        String profileDirPrefix = System.getProperty("user.home");
        Path reqFile = Path.of(profileDirPrefix, TAPIS_CMD_JWT_PROFILE_DIR,profileName + PROFILE_EXT);
        String reqString = Files.readString(reqFile);
        
        // Populate and return the properties.
        var properties = new Properties();
        properties.load(new StringReader(reqString));
        validate(properties, reqFile, addV3ToBaseUrl);
        return properties;
    }
    
    /** Reads the contents of a cred file and returns it as a string
     * 
     * @param fullPathToCredFile full path with extension of the cred file to be read
     * @return the contents of the cred file as a string
     * @throws Exception
     */
    public static String getCredFile(String fullPathToCredFile) throws Exception
    {
        Path credFilePath = Path.of(fullPathToCredFile);
        return (new String(Files.readString(credFilePath)));
    }
    
    /** Sets the default headers for X-TAPIS-USER and X-TAPIS-TENANT, which are used for 
     * obo client operations
     * 
     * @param client the client that is being used as an extension of ITapisClient
     * @param oboUser the username to operate on-behalf-of
     * @param oboTenant the tenant to operate on-behalf-of
     */
    public static void setOboHeaders(ITapisClient client, String oboUser, String oboTenant)
    {
        if (!StringUtils.isBlank(oboUser)) { 
            client.addDefaultHeader("X-TAPIS-USER", oboUser);
            client.addDefaultHeader("X-TAPIS-TENANT", oboTenant);
        }
    }
    
    /** Reads contents of a json request file, looking first in $HOME/TapisCmd/requests
     * and second in tapis-cmd/src/main/java/edu/utexas/tacc/tapis/cmd/driver/requests
     * 
     * NOTE: for CLI commands request files must be in $HOME/TapisCmd/requests
     *       
     *       for IDE commands request files can be in $HOME/TapisCmd/requests,
     *       as well as tapis-cmd/src/main/java/edu/utexas/tacc/tapis/cmd/driver/requests
     * 
     * @param reqFileName the name of the json request file with .json extension
     * @return the contents of the json file to be used for the request object
     * @throws Exception
     */
    public static String readRequestFile(String reqFileName) throws Exception
    {
    	Path homeDirPath = Path.of(System.getProperty("user.home") + "/" + TAPIS_CMD_REQUEST_HOME_DIR + "/" + reqFileName);
    	Path userDirPath = Path.of(System.getProperty("user.dir") + "/" + REQUEST_SUBDIR + "/" + reqFileName);
    	
    	if(Files.isReadable(homeDirPath))
    		return Files.readString(homeDirPath);
    	else if(!Files.isReadable(userDirPath))
    		throw new Exception("Request File: " + reqFileName + " could not be found in $HOME/TapisCmd/requests or in tapis-cmd/src/main/java/.../driver/requests \nTHROWING ERROR");
    	else
    		return Files.readString(userDirPath);
    }
    
    /** Basic validation required by all apis. Also append v3 to base url if required.
     * 
     * @param props the jwt property file information
     * @param reqFile the jwt property file path
     * @param addV3ToBaseUrl whether to fix up base url
     */
    private static void validate(Properties props, Path reqFile, boolean addV3ToBaseUrl)
    {
        // BASE_URL is required.
        String url = props.getProperty("BASE_URL");
        if (StringUtils.isBlank(url))
            throw new IllegalArgumentException("BASE_URL must be assigned in JWT property file: " + reqFile.toFile().getAbsolutePath());
        if (addV3ToBaseUrl && !url.endsWith("/v3")) {
            url += "/v3";
            props.setProperty("BASE_URL", url);
        }

        // USER_JWT is required.
        String jwt = props.getProperty("USER_JWT");
        if (StringUtils.isBlank(jwt))
            throw new IllegalArgumentException("USER_JWT must be assigned in JWT property file: " + reqFile.toFile().getAbsolutePath());
    }
}
