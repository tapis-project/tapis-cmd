package edu.utexas.tacc.tapis.cmd.driver;

import java.time.LocalDateTime;
import java.util.Formatter;
import java.util.Properties;

import edu.utexas.tacc.tapis.client.shared.exceptions.TapisClientException;
import edu.utexas.tacc.tapis.jobs.client.JobsClient;
import edu.utexas.tacc.tapis.jobs.client.gen.model.Job;
import edu.utexas.tacc.tapis.jobs.client.gen.model.ReqSubmitJob;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;

public class JobsSubmitLoadTest
{
    /**This test submits a number of job  with a max and min times using locally created and operated systems, apps and job definitions. 
     * 
     * JobSubmitLoadTest
     *  -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -req <name of json request file located in driver/requests> -maxTime <max job runtime>
     *  -minTime <min job runtime> -numJobs <number of jobs to run serially on local machine>
     */
    
    // The placeholder text that gets substituted by a pseudo-random number of seconds.
    private static final String PLACEHOLDER_SECONDS = "${SLEEP_SECS}";
    
    // Ouput record format string.
    private static final String FORMAT = "\n%-7s %-11s %-11s %-18s";
    
    // Counter.
    private static int jobsSubmitted;
    
    public static void main(String[] args) throws Exception
    {
    	//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for JobSubmit has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.reqFilename == null)
    		throw new Exception("reqFilename is null and is required for JobsLoadTest operation, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for JobsLoadTes operation, THROWING ERROR");
    	
    	if(parms.maxSecs <= 0)
    		throw new Exception("maxTime is null and is required for JobsLoadTest operation, THROWING ERROR");
    	
    	if(parms.minSecs <= 0)
    		throw new Exception("minTime is null and is required for JobsLoadTest operation, THROWING ERROR");
    	
    	if(parms.numJobs <= 0)
    		throw new Exception("numJobs is null and is required for JobsLoadTest operation, THROWING ERROR");
    	
        //----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        String url = props.getProperty("BASE_URL");
        if (!url.endsWith("/v3")) url += "/v3";
    
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create a job client.
        var jobClient = new JobsClient(url, props.getProperty("USER_JWT"));

        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
            TestUtils.setOboHeaders(jobClient, parms.oboUser, parms.oboTenant);

        // Get the request json as a string.
        String json = TestUtils.readRequestFile(parms.reqFilename);
        
        // Informational message.
        printHeader(parms, url);
        
    	for (int i = 0; i < parms.numJobs; i++) 
    	{
    		//----------------------- READ JSON REQUEST INTO REQ OBJECT -----------------------//
    		// Read request file into a string.
    		int sleepSecs = getSleepSecs(parms);
    		String finalReqJson = json.replace(PLACEHOLDER_SECONDS, Integer.toString(sleepSecs));
    		
    		// Convert json string into a job submission request.
    		ReqSubmitJob submitReq = TapisGsonUtils.getGson().fromJson(finalReqJson, ReqSubmitJob.class);
          
    		//----------------------- USE CLIENT OBJECT -----------------------//
    		try {
    		    Job job = jobClient.submitJob(submitReq);
    		    
                ResultRecord rec = new ResultRecord();
                rec.seqNo = i + 1;
                rec.sleepSecs = sleepSecs;
                rec.httpStatusCode = 200;
    		    printJobRecord(rec);

    		} catch (TapisClientException e) {
                ResultRecord rec = new ResultRecord();
                rec.seqNo = jobsSubmitted + 1;
                rec.sleepSecs = sleepSecs;
                rec.httpStatusCode = e.getCode();
                rec.message = e.getTapisMessage();
                printJobRecord(rec);

                String msg = "\n\nJob submission aborted after " + rec.seqNo + " attempts.";
                System.out.println(msg);
                System.out.println("End time: " + LocalDateTime.now());
                throw e;
    		}
    		
    		// Increment successful submission count.
    		jobsSubmitted++;
    	}
    	
        // Add a new line to the output.
        System.out.println("\n\nJobs to submit = " + parms.numJobs + 
                           ", submissions attempted = " + jobsSubmitted + ".");
        System.out.println("End time: " + LocalDateTime.now());
    }

    /* ---------------------------------------------------------------------- */
    /* getSleepSecs:                                                          */
    /* ---------------------------------------------------------------------- */
    private static int getSleepSecs(CMDUtilsParameters parms)
    {
        // Customize the job's sleep time.
        int sleepSecs;
        if (parms.minSecs == parms.maxSecs) sleepSecs = parms.maxSecs;
            else {
                // 0 >= d < 1, so multiplying d times the min/max difference and
                // added that value to min yields a number between min and max.
                double d = Math.random();
                sleepSecs = parms.minSecs + (int)((parms.maxSecs - parms.minSecs) * d);
                if (sleepSecs == 0) sleepSecs = parms.maxSecs; // in case d == 0
            }
        return sleepSecs;
    }

    /* ---------------------------------------------------------------------- */
    /* printHeader:                                                           */
    /* ---------------------------------------------------------------------- */
    private static void printHeader(CMDUtilsParameters parms, String url)
    {
        System.out.println("Processing " + parms.reqFilename + ".");
        System.out.println("Contacting Jobs Service at " + url + ".");
        System.out.println("Number of jobs: " + parms.numJobs
                           + ", minSeconds: " + parms.minSecs
                           + ", maxSeconds: " + parms.maxSecs);
        // First lines of output.
        StringBuilder buf = new StringBuilder();
        buf.append("\n");
        buf.append(JobsSubmitLoadTest.class.getSimpleName());
        buf.append(": Number of jobs to submit = ");
        buf.append(parms.numJobs);
        buf.append(".\n");
        buf.append("Minimum runtime = ");
        buf.append(parms.minSecs);
        buf.append(".\n");
        buf.append("Maximum runtime = ");
        buf.append(parms.maxSecs);
        buf.append(".\n");
        buf.append("URL Posted:    ");
        buf.append(url);
        buf.append("\n");
        buf.append("Start time: ");
        buf.append(LocalDateTime.now());
        buf.append("\n");
        
        // Create the formatter and add header lines.
        Formatter f = new Formatter(buf);
        f.format(FORMAT, "SeqNo", "SleepSecs", "HTTP Code", "Response Message");
        f.format(FORMAT, "-----", "---------", "---------", "----------------");
        f.close();
        
        // Print the buffer.
        System.out.print(buf.toString());
    }

    /* ---------------------------------------------------------------------- */
    /* printJobRecord:                                                           */
    /* ---------------------------------------------------------------------- */
    private static void printJobRecord(ResultRecord rec)
    {
        // Convert field records.
        String seqNo = Integer.toString(rec.seqNo);
        String sleepSecs = Integer.toString(rec.sleepSecs);
        String httpStatusCode = Integer.toString(rec.httpStatusCode);
        String httpReasonPhrase = rec.message == null ? "" : rec.message;
        
        // Create a buffer and a formatter.
        StringBuilder buf = new StringBuilder();
        Formatter f = new Formatter(buf);
        f.format(FORMAT, seqNo, sleepSecs, httpStatusCode, httpReasonPhrase);
        f.close();
        
        // Print the record.
        System.out.print(buf.toString());
    }
    
    /* ********************************************************************** */
    /*                            ResultRecord Class                          */
    /* ********************************************************************** */
    private static final class ResultRecord
    {
        private int seqNo;
        private int sleepSecs;
        private int httpStatusCode;
        private String message;
    }
}