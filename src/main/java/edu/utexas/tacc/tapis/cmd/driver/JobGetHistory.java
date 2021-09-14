package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;
import java.lang.Exception;

import edu.utexas.tacc.tapis.jobs.client.JobsClient;

public class JobGetHistory 
{
	/** gets job history 
     * 
     * JobGetHistory -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -jobUuid <Uuid of job> -limit <integer used for number of files to list>
     * -skip <where in job list to begin history> -orderBy <field to order history by> -totalCount <used as an integer for jobGetHistory>
     */
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for JobGetHistory has failed",e);
        }
		
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.jobUuid == null)
    		throw new Exception("jobName is null and is required for JobGetHistory operation, THROWING ERROR");
    	
    	if(parms.limit == null)
    		throw new Exception("limit is null and is required for JobGetHistory operation, THROWING ERROR");
    	
    	if(parms.orderBy == null)
    		throw new Exception("orderBy is null and is required for JobGetHistory operation, THROWING ERROR");
    	
    	if(parms.skip == null)
    		throw new Exception("skip is null and is required for JobGetHistory operation, THROWING ERROR");
    	
    	if(parms.totalCount == null)
    		throw new Exception("totalCount is null and is required for JobGetHistory operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for JobGetHistory operation, THROWING ERROR");
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE AND USE CLIENT OBJECT -----------------------//
        var jobClient = new JobsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
        var job = jobClient.getJobHistory(parms.jobUuid, Integer.parseInt(parms.limit), parms.orderBy, Integer.parseInt(parms.skip), null, Integer.parseInt(parms.totalCount));
        System.out.println(job.toString());
	}
}
