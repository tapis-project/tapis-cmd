package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.jobs.client.JobsClient;

public class JobGet 
{
	/** gets job 
     * 
     * JobGet -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -jobUuid <Uuid of job>
     */
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for JobGet has failed");
        }
		
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.jobUuid == null)
    		throw new Exception("jobUuid is null and is required for JobGet operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for JobGet operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE AND USE CLIENT OBJECT -----------------------//
        var jobClient = new JobsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        var job = jobClient.getJob(parms.jobUuid);
        System.out.println(job.toString());
	}
}
