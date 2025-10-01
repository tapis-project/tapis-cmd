package edu.utexas.tacc.tapis.cmd.driver;

import java.util.Properties;

import edu.utexas.tacc.tapis.jobs.client.JobsClient;
import edu.utexas.tacc.tapis.jobs.client.gen.model.JobAnnotationDisplay;
import edu.utexas.tacc.tapis.jobs.client.gen.model.ReqJobAnnotation;
import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;

/**
 * JobsAnnotations -jwt <jwt filename located in $HOME/TapisCmd/jwt> -jobUuid
 * <job UUID> -req <payload file located in $HOME/TapisCmd/requests>
 */
public class JobsAnnotations {
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

        if (parms.reqFilename == null)
            throw new Exception("reqFilename is null and is required for JobSubmit operation, THROWING ERROR");

        if (parms.jwtFilename == null)
            throw new Exception("jwtFilename is null and is required for JobSubmit operation, THROWING ERROR");

        // ----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename, true);
        String url = props.getProperty("BASE_URL");

        // Informational message.
        System.out.println("Processing " + parms.reqFilename + ".");
        System.out.println("Contacting Jobs Service at " + url + ".");

        // ----------------------- READ JSON REQUEST INTO REQ OBJECT
        // -----------------------//
        // Convert json string into a job submission request.
        ReqJobAnnotation submitReq = TapisGsonUtils.getGson().fromJson(DriverUtils.readRequestFile(parms.reqFilename),
                ReqJobAnnotation.class);

        // ----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create a job client.
        var jobClient = new JobsClient(url, props.getProperty("USER_JWT"));

        // ----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if (parms.oboTenant != null)
            DriverUtils.setOboHeaders(jobClient, parms.oboUser, parms.oboTenant);

        // ----------------------- USE CLIENT OBJECT -----------------------//

        JobAnnotationDisplay result = switch (parms.operation.toLowerCase()) {
            case "patch":
                yield jobClient.patchJobAnnotations(parms.jobUuid, submitReq);
            case "put":
                yield jobClient.putJobAnnotations(parms.jobUuid, submitReq);
            default:
                throw new IllegalArgumentException("Invalid operation: " + parms.operation +
                        ". Must be 'patch' or 'put'");
        };

        System.out.println(TapisGsonUtils.getGson(true).toJson(result));
        System.out.println();
        System.out.println("--------------------------------------------------------------------");
        System.out.println(result.getNotes());
        System.out.println(result.getTags());
        System.out.println("--------------------------------------------------------------------");
        System.out.println("Finished processing " + parms.operation + " for job " + parms.jobUuid);

    }
}
