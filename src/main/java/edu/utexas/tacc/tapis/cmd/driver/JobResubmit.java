package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.jobs.client.JobsClient;
import edu.utexas.tacc.tapis.jobs.client.gen.model.Job;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;

public class JobResubmit 
{
    /**This test submits a job using previously created systems, apps and job definitions. 
     * 
     * JobSubmit -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -req <name of json request file located in driver/requests>
     */
    public static void main(String[] args) throws Exception
    {
    	//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for JobSubmit has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.jobUuid == null)
    		throw new Exception("jobUuid is null and is required for JobSubmit operation, THROWING ERROR");
    	
        //----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename, true);
        String url = props.getProperty("BASE_URL");
        
        // Informational message.
        System.out.println("Contacting Jobs Service at " + url + ".");
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create a job client.
        var jobClient = new JobsClient(url, props.getProperty("USER_JWT"));
//        jobClient.addDefaultHeader("Content-Type", "application/json");
  
        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
        	DriverUtils.setOboHeaders(jobClient, parms.oboUser, parms.oboTenant);
        
        //----------------------- USE CLIENT OBJECT -----------------------//
        Job job = jobClient.resubmitJob(parms.jobUuid);
        System.out.println(TapisGsonUtils.getGson(true).toJson(job));
        System.out.println();
        System.out.println("--------------------------------------------------------------------");
        System.out.println(job.getParameterSet());
        System.out.println(job.getUuid());
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Finished processing " + parms.reqFilename);
    }
}
