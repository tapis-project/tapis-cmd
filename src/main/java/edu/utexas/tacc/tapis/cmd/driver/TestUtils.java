package edu.utexas.tacc.tapis.cmd.driver;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import edu.utexas.tacc.tapis.client.shared.ITapisClient;

public class TestUtils 
{
    public static final String TAPIS_CMD_JWT_PROFILE_DIR = "TapisCmd/jwt";
    public static final String TAPIS_CMD_REQUEST_HOME_DIR = "TapisCmd/requests";
    public static final String REQUEST_SUBDIR = "requests";
    
    // Profile file name extension.
    public static final String PROFILE_EXT = ".properties";
    public static final String PEM_EXT = ".pem";
    
    /** Load the contents of the properties file into memory.  The property file
     * is expected to contain two key/value pairs:
     * 
     *      BASE_URL=<the base url of the jobs service>
     *      USER_JWT=<the requestor's JWT>
     * 
     * @param profileName the file name without extension
     * @return the properties object populated from file
     * @throws IOException on error
     */
    public static Properties getTestProfile(String profileName) throws IOException
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
        return properties;
    }
    
    
    public static String getCredFile(String fileName) throws Exception
    {
        Path credFilePath = Path.of(fileName);
        return (new String(Files.readString(credFilePath)));
    }
    
    public static void setOboHeaders(ITapisClient client, String oboUser, String oboTenant)
    {
        if (!StringUtils.isBlank(oboUser)) { 
            client.addDefaultHeader("X-TAPIS-USER", oboUser);
            client.addDefaultHeader("X-TAPIS-TENANT", oboTenant);
        }
    }
    
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
}
