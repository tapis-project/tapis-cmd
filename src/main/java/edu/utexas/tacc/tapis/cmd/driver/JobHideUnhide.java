package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.jobs.client.JobsClient;
import edu.utexas.tacc.tapis.jobs.client.gen.model.JobHideDisplay;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;

/**
 * JobsHideUnhide -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -jobUuid
 * <job UUID> -operation <hide|unhide>
 */
public class JobHideUnhide {
    public static void main(String[] args) throws Exception {
        // ----------------------- INITIALIZE PARMS -----------------------//
        CMDUtilsParameters parms = null;
        try {
            parms = new CMDUtilsParameters(args);
        } catch (Exception e) {
            throw new Exception("Parms initialization for JobSubmit has failed with Exception: ", e);
        }

        // ----------------------- VALIDATE PARMS -----------------------//
        if (parms.jobUuid == null)
            throw new Exception("jobUuid is null and is required for JobHideUnhide operation, THROWING ERROR");

        if (parms.operation == null)
            throw new Exception("operation is null and is required for JobHideUnhide operation, THROWING ERROR");

        if (parms.jwtFilename == null)
            throw new Exception("jwtFilename is null and is required for JobHideUnhide operation, THROWING ERROR");

        // ----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename, true);
        String url = props.getProperty("BASE_URL");

        // Informational message.
        System.out.println("Processing " + parms.reqFilename + ".");
        System.out.println("Contacting Jobs Service at " + url + ".");

        // ----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create a job client.
        var jobClient = new JobsClient(url, props.getProperty("USER_JWT"));

        // ----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if (parms.oboTenant != null)
            DriverUtils.setOboHeaders(jobClient, parms.oboUser, parms.oboTenant);

        // ----------------------- USE CLIENT OBJECT -----------------------//

        JobHideDisplay result = switch (parms.operation.toLowerCase()) {
            case "hide":
                yield jobClient.hideJob(parms.jobUuid);

            case "unhide":
                yield jobClient.unhideJob(parms.jobUuid);
            default:
                throw new IllegalArgumentException("Invalid operation: " + parms.operation +
                        ". Must be 'hide' or 'unhide'");
        };

        System.out.println(TapisGsonUtils.getGson(true).toJson(result));
        System.out.println();
        System.out.println("--------------------------------------------------------------------");
        System.out.println(result.getMessage());
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Finished processing " + parms.operation + " for job " + parms.jobUuid);
    }
}
