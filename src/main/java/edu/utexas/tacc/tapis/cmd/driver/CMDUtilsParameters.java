package edu.utexas.tacc.tapis.cmd.driver;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;

import javax.ws.rs.core.Response.Status;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import edu.utexas.tacc.tapis.shared.exceptions.TapisImplException;
import edu.utexas.tacc.tapis.shared.i18n.MsgUtils;

public final class CMDUtilsParameters 
{
	/* ********************************************************************** */
	/*                               Constants                                */
	/* ********************************************************************** */
	//Name Lengths
	public static final int MAX_NAME_LEN = 255;
	public static final int MIN_NAME_LEN = 1;

	/* ********************************************************************** */
	/*                                 Fields                                 */
	/* ********************************************************************** */
	@Option(name = "-app", required = false, aliases = {"-application","-a"},
			metaVar = "<appName>", usage = "an application name")
	public String appName;
	
	@Option(name = "-appVersion", required = false, aliases = {"-version","-appv","-v"},
			metaVar = "<appVersion>", usage = "an application version")
	public String appVersion;
	
	@Option(name = "-file", required = false, aliases = {"-filename","-f"},
			metaVar = "<fileName>", usage = "a file name")
	public String fileName;
	
	@Option(name = "-help", required = false, aliases = {"--help","-h"},
			metaVar = "<help>", usage = "display help message")
	public boolean help;
	
	@Option(name = "-job", required = false, aliases = {"-jobName"},
			metaVar = "<jobName>", usage = "a job name")
	public String jobName;
	
	@Option(name = "-jobUuid", required = false, 
			metaVar = "<jobUuid>", usage = "a job uuid")
	public String jobUuid;
	
	@Option(name = "-jwtFile", required = false, aliases = {"-jwt","-j"},
			metaVar = "<jwtFilename>", usage = "name for jwt file in $HOME/Tapiscmd/jwt")
	public String jwtFilename;
	
	@Option(name = "-limit", required = false, aliases = {"-lim"},
			metaVar = "<limit>", usage = "used as integer in FilesList & JobGetHistory")
	public int limit = 1000;
	
	@Option(name = "-meta", required = false,
			metaVar = "<meta>", usage = "used as a boolean in FilesList")
	public boolean meta = false;
	
	@Option(name = "-oboTenant", required = false, aliases = {"-oboT","-oTenant"},
			metaVar = "<oboTenant>", depends = {"-oboUser"}, usage = "name field for tenant portion of obo")
	public String oboTenant;
	
	@Option(name = "-oboUser", required = false, aliases = {"-oboU","-oUser"},
			metaVar = "<oboUser>", depends = {"-oboTenant"}, usage = "name field for user portion of obo")
	public String oboUser;
	
	@Option(name = "-offset", required = false, aliases = {"-off"},
			metaVar = "<offset>", usage = "used as long in FilesList")
	public long offset = 0;
	
	@Option(name = "-orderBy", required = false,
			metaVar = "<orderBy>", usage = "key to order jobs by")
	public String orderBy;
	
	@Option(name = "-path", required = false, aliases = {"-pathName"},
			metaVar = "<pathName>", usage = "a pathname pointing to directory")
	public String pathName;
	
	@Option(name = "-privKey", required = false, aliases = {"-priv","-privateKey","-private"},
			metaVar = "<privKey>", usage = "a full path to the private key, used in systemCreate")
	public String privKey;
	
	@Option(name = "-pubKey", required = false, aliases = {"-pub","-publicKey","public"},
			metaVar = "<pubKey>", usage = "a full path to the public key, used in systemCreate")
	public String pubKey;
	
	@Option(name = "-req", required = false, aliases = {"-reqfile","-r"},
			metaVar = "<reqFilename>", usage = "a request file name in $HOME/Tapiscmd/requests")
	public String reqFilename;
	
	@Option(name = "-schedulerName", required = false, aliases = {"-scheduler","-schedName"},
			metaVar = "<schedulerName>", usage = "used as a name for scheduler profiles")
	public String schedulerName;
	
	@Option(name = "-skip", required = false,
			metaVar = "<skip>", usage = "used as an integer in Jobs")
	public int skip = 0;
	
	@Option(name = "-startAfter", required = false,
			metaVar = "<startAfter>", usage = "used as a string in Jobs")
	public String startAfter;
	
	@Option(name = "-system", required = false, aliases = {"-sys","-s"},
			metaVar = "<systemName>", usage = "a system name")
	public String systemName;
	
	@Option(name = "-totalCount", required = false, aliases = {"-total","count"},
			metaVar = "<totalCount>", usage = "used as an integer in Jobs")
	public int totalCount = 1000;
	
	@Option(name = "-user", required = false, aliases = {"-username","-u"},
			metaVar = "<userName>", usage = "a user name")
	public String userName;
	
	@Option(name = "-zip", required = false,
			metaVar = "<zipFlag>", usage = "used as boolean in fileGetContents")
	public boolean zipFlag;
	
	/* ********************************************************************** */
	/*                              Constructors                              */
	/* ********************************************************************** */
	/* ---------------------------------------------------------------------- */
	/* constructor:                                                           */
	/* ---------------------------------------------------------------------- */
	public CMDUtilsParameters(String[] args)
      throws TapisImplException
	{
		initializeParms(args);
		validateParms();
	}
	/* **************************************************************************** */
	/*                               Private Methods                                */
	/* **************************************************************************** */
	/* ---------------------------------------------------------------------------- */
	/* initializeParms:                                                             */
	/* ---------------------------------------------------------------------------- */
	private void initializeParms(String[] args)
	  throws TapisImplException
	{
		// Get a command line parser to verify input.
	    CmdLineParser parser = new CmdLineParser(this);
	    parser.getProperties().withUsageWidth(120);
	    
	    try {
	       // Parse the arguments.
	       parser.parseArgument(args);
	    }
	    catch (CmdLineException e)
	    {  
	    	if(!help)
	    	{
	    		// Create message buffer of sufficient size.
	            final int initialCapacity = 1024;
	            StringWriter writer = new StringWriter(initialCapacity);
	          
	            // Write parser error message.
	            writer.write("\n******* Input Parameter Error *******\n");
	            writer.write(e.getMessage());
	            writer.write("\n\n");
	          
	            // Write usage information--unfortunately we need an output stream.
	            writer.write("Tapis Command Utils [options...]\n");
	            ByteArrayOutputStream ostream = new ByteArrayOutputStream(initialCapacity);
	            parser.printUsage(ostream);
	            try {writer.write(ostream.toString("UTF-8"));}
	                catch (Exception e1) {}
	            writer.write("\n");
	          
	            // Throw exception.
	            throw new TapisImplException(writer.toString(), Status.BAD_REQUEST.getStatusCode());
	        }
	    	
	    	if(help)
	    	{
	    		System.out.println("\nCommandUtils for CRUD testing and dev");
	    		System.out.println("\nCommandUtils [options...]\n");
	    		parser.printUsage(System.out);
	    		System.exit(0);
	    	}
	    }
	}
    	
	/* ---------------------------------------------------------------------- */
	/* validateParms:                                                         */
	/* ---------------------------------------------------------------------- */
	/** Check the semantic integrity of the input parameters.
	 * 
	 * @throws JobException
	 */
	private void validateParms()
	  throws TapisImplException
	{
		//----------------------------------------------------------------------------
		// name length checks.
		//----------------------------------------------------------------------------
		//jwtFilename check
	    if ((jwtFilename != null) && (jwtFilename.length() > MAX_NAME_LEN || jwtFilename.length() < MIN_NAME_LEN))
	    {
	        String msg = MsgUtils.getMsg("TAPIS_PARAMETER_OUT_OF_RANGE", "jwtFilename", jwtFilename.length(), MIN_NAME_LEN, MAX_NAME_LEN);
	        throw new TapisImplException(msg,Status.BAD_REQUEST.getStatusCode());
	    }
	    
	    //systemName check
	    if ((systemName != null) && (systemName.length() > MAX_NAME_LEN || systemName.length() < MIN_NAME_LEN))
	    {
	        String msg = MsgUtils.getMsg("TAPIS_PARAMETER_OUT_OF_RANGE", "systemName", systemName.length(), MIN_NAME_LEN, MAX_NAME_LEN);
	        throw new TapisImplException(msg,Status.BAD_REQUEST.getStatusCode());
	    }
	    
	    //appName check
	    if ((appName != null) && (appName.length() > MAX_NAME_LEN || appName.length() < MIN_NAME_LEN))
	    {
	        String msg = MsgUtils.getMsg("TAPIS_PARAMETER_OUT_OF_RANGE", "appName", appName.length(), MIN_NAME_LEN, MAX_NAME_LEN);
	        throw new TapisImplException(msg,Status.BAD_REQUEST.getStatusCode());
	    }
	    
	    //appVersion check
	    if ((appVersion != null) && (appVersion.length() > MAX_NAME_LEN || appVersion.length() < MIN_NAME_LEN))
	    {
	        String msg = MsgUtils.getMsg("TAPIS_PARAMETER_OUT_OF_RANGE", "appVersion", appVersion.length(), MIN_NAME_LEN, MAX_NAME_LEN);
	        throw new TapisImplException(msg,Status.BAD_REQUEST.getStatusCode());
	    }
	    
	    //userName check
	    if ((userName != null) && (userName.length() > MAX_NAME_LEN || userName.length() < MIN_NAME_LEN))
	    {
	        String msg = MsgUtils.getMsg("TAPIS_PARAMETER_OUT_OF_RANGE", "userName", userName.length(), MIN_NAME_LEN, MAX_NAME_LEN);
	        throw new TapisImplException(msg,Status.BAD_REQUEST.getStatusCode());
	    }
	    
	    //fileName check
	    if ((fileName !=null) && (fileName.length() > MAX_NAME_LEN || fileName.length() < MIN_NAME_LEN))
	    {
	        String msg = MsgUtils.getMsg("TAPIS_PARAMETER_OUT_OF_RANGE", "fileName", fileName.length(), MIN_NAME_LEN, MAX_NAME_LEN);
	        throw new TapisImplException(msg,Status.BAD_REQUEST.getStatusCode());
	    }
	    
	    //----------------------------------------------------------------------------
	    // TODO: ADD SYNTAX CHECKS ON PATH AND FILENAMES FROM PATHSANITIZER
	    //----------------------------------------------------------------------------
	}
}
