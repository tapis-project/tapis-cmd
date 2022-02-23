package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.jobs.client.JobsClient;
import edu.utexas.tacc.tapis.jobs.client.gen.model.Job;
import edu.utexas.tacc.tapis.jobs.client.gen.model.ReqSubmitJob;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;

public class JobSubmitAndGet 
{
	/**This test submits a job using previously created systems, apps and job definitions. 
     * 
     * JobSubmitAndGet -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -req <name of json request file located in driver/requests>
     */
    public static void main(String[] args) throws Exception
    {
    	//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for JobSubmitAndGet has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.reqFilename == null)
    		throw new Exception("reqFilename is null and is required for JobSubmit operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for JobSubmit operation, THROWING ERROR");

    	//----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename, true);
        String url = props.getProperty("BASE_URL");
        
        System.out.println("Processing " + parms.reqFilename + ".");
        
        //----------------------- READ JSON REQUEST INTO REQ OBJECT -----------------------//
        ReqSubmitJob submitReq = TapisGsonUtils.getGson().fromJson(DriverUtils.readRequestFile(parms.reqFilename), ReqSubmitJob.class);

        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create a job client.
        var jobClient = new JobsClient(url, props.getProperty("USER_JWT"));
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	DriverUtils.setOboHeaders(jobClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT TO SUBMIT -----------------------//
        Job job = jobClient.submitJob(submitReq);
        System.out.println(TapisGsonUtils.getGson(true).toJson(job));
        System.out.println();
        System.out.println("--------------------------------------------------------------------");
        System.out.println(job.getParameterSet());
        System.out.println(job.getUuid());
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Finished processing " + parms.reqFilename);
        
        //----------------------- USE CLIENT OBJECT TO GET -----------------------//
        System.out.println("Getting job: " + job.getName() + " \n" + "with Uuid: " + job.getUuid());
        var getJob = jobClient.getJob(job.getUuid());
        System.out.println(getJob.toString());
    }
}
