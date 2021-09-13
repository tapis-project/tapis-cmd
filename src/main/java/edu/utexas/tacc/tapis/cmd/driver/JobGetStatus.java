package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.jobs.client.JobsClient;

public class JobGetStatus 
{
	/** get job status
     * 
     * JobGetStatus -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -jobUuid <Uuid of job>
     */

	public static void main(String[] args) throws Exception 
	{
	    CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for JobGetStatus has failed");
        }
		
    	if(parms.jobUuid == null)
    		throw new Exception("jwtFilename is null and is required for JobGetStatus operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for JobGetStatus operation, THROWING ERROR");
    	
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        var jobClient = new JobsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        var job = jobClient.getJobStatus(parms.jobUuid);
        System.out.println(job.toString());
		
	}

}
