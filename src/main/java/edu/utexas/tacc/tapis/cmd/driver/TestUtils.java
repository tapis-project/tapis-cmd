package edu.utexas.tacc.tapis.cmd.driver;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import edu.utexas.tacc.tapis.client.shared.ITapisClient;

public class TestUtils 
{
    // The profiles are always in the $HOME/TapisProfiles directory.
    // The profiles contain the BASE_URL and USER_JWT key/value pairs.
    public static String PROFILE_DIRECTORY = "TapisProfiles";
    //TODO: NEED TO CHANGE THESE PREFIXES ONCE TAPIS-CMD IS MOVED TO OWN REPO OUT OF SHARED
    public static String TAPIS_CMD_JWT_PROFILE_DIR = "TapisCmd/jwt";
    
    // Profile file name extension.
    public static String PROFILE_EXT = ".properties";
    public static String PEM_EXT = ".pem";
    
    // Pattern used to split the obo string by its default separator "-"
    public static Pattern dashPattern = Pattern.compile("-");
    
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
    	
    	// TODO: CHECK WHETHER USER.DIR PASSES THROUGH CORRECTLY WHEN RUNNING THROUGH THE JAR, CAN MOST LIKELY DEPEND ON CLASSPATH
        String profileDirPrefix = System.getProperty("user.home");
        Path reqFile = Path.of(profileDirPrefix, TAPIS_CMD_JWT_PROFILE_DIR,profileName + PROFILE_EXT);
        String reqString = Files.readString(reqFile);
        
        // Populate and return the properties.
        var properties = new Properties();
        properties.load(new StringReader(reqString));
        return properties;
    }
    
    /** Return the template for the profile path. */
    public static String getProfilePathTemplate()
    {
        return "$HOME/" + PROFILE_DIRECTORY + "/<profileName>.properties";
    }
    
    
    public static String getCredFile(String fileName)
    {
        Path credFilePath = Path.of(fileName);
    	
        try {
			String credContents = new String(Files.readString(credFilePath));
			//System.out.println(credContents);
			return credContents;
        } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return "ERROR IN READING IN KEY FROM PEM";
    }
    
    public static void setOboHeaders(ITapisClient client, String oboUser, String oboTenant)
    {
        if (!StringUtils.isBlank(oboUser)) { 
            client.addDefaultHeader("X-TAPIS-USER", oboUser);
            client.addDefaultHeader("X-TAPIS-TENANT", oboTenant);
        }
    }
    
    
    
}
