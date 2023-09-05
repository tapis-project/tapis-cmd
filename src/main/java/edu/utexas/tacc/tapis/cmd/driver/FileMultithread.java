package edu.utexas.tacc.tapis.cmd.driver;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import edu.utexas.tacc.tapis.files.client.FilesClient;
import edu.utexas.tacc.tapis.files.client.gen.model.FileInfo;
import edu.utexas.tacc.tapis.shared.utils.SimpleTimer;


public class FileMultithread 
{
	/** Tests the 
     * 
     * FileMultithread -jwt dev-dev-testuser2 -system tapisv3-exec -totalCount 100
     */
	
	//----------------------- COMPILE-TIME PARMS ---------------------//
	// Set to true to run with a single FilesClient, false for each thread 
	// to have use its own client. 
	private static final boolean SINGLE_CLIENT = false; 
	private static final int DEFAULT_THREAD_NUM = 30;
	
	// The shared client when running in single client mode.
	private static FilesClient _filesClient;
	
	public static void main(String[] args) throws Exception
	{
		//----------------------- INITIALIZE PARMS -----------------------//
		CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for FileMultithread has failed with Exception: ",e);
        }

    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for FileMultithread operation, THROWING ERROR");
    	
    	if(parms.systemName == null)
    		throw new Exception("systemName is null and is required for FileMultithread operation, THROWING ERROR");
    	
    	// The parameter file default is too many.
    	if (parms.totalCount == 1000) parms.totalCount = DEFAULT_THREAD_NUM;
    	
    	//----------------------- READ IN JWT PROFILE -----------------------//
    	// Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename);
    	
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create client in single client mode only.
        if (SINGLE_CLIENT) {
        	_filesClient = new FilesClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
  
        	//----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        	if (parms.oboTenant != null)
        	DriverUtils.setOboHeaders(_filesClient, parms.oboUser, parms.oboTenant);
        }
        
        //----------------------- SPAWN THREADS ------------------------------//
        // Create a list of child threads for joining.
        var childThreads = new ArrayList<ClientThread>(parms.totalCount);
        
        // Spawn each thread after capturing the current time.
        var mode = SINGLE_CLIENT ? "single" : "multiple";
        SimpleTimer totalTime = SimpleTimer.start("TotalTime");
        System.out.println("Start time for " + parms.totalCount + " threads in " 
                           + mode + " client mode: "+ totalTime.getStartTime());
        for (int i = 0; i < parms.totalCount; i++) {
        	var t = new ClientThread(parms, props, i);
        	t.start();
        	childThreads.add(t);
        }
        
        // Wait until all children have completed.
        for (var t : childThreads) t.join();
        System.out.println("Total elapsed seconds: " + totalTime.getElapsedSeconds());
	}
	
	/** The actual class that makes a client call on its own thread. 
 	 * 
	 */
	private static final class ClientThread extends Thread
	{
		// Fields
		private CMDUtilsParameters parms;
		private Properties props;
		private int tnum;
		
		// Constructor.
		private ClientThread(CMDUtilsParameters parms, Properties props, int tnum) {
			this.parms = parms;
			this.props = props;
			this.tnum  = tnum;
		}
		
		public void run() {
			// See if we need to create our own client.
			FilesClient filesClient;
			if (SINGLE_CLIENT) filesClient = _filesClient;
			 else {
				 // Private client.
				 filesClient = new FilesClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));
		         if (parms.oboTenant != null)
		          	DriverUtils.setOboHeaders(filesClient, parms.oboUser, parms.oboTenant);
			 }
		
			// Hardcode call parameters.
			var pathName = "/";
			int limit = 100;
			int offset = 0;
			var recurse = false;

			// Time the client call.
			var start = Instant.now();
			try {
				var list = filesClient.listFiles(parms.systemName, pathName, limit, offset, recurse);
				// printList(list);  // Uncomment for debugging.
			} catch (Exception e) {e.printStackTrace();}
			
			// Print runtime before terminating.
			var stop = Instant.now();
			System.out.println("thread" + tnum + " (ms):\t" + Duration.between(start, stop).toMillis());
		}
		
		// For debugging.
		private void printList(List<FileInfo> list) {
			for (var item : list) System.out.println(item);
		}
		
	}
}
