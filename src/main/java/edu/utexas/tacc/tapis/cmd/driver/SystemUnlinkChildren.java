package edu.utexas.tacc.tapis.cmd.driver;

import edu.utexas.tacc.tapis.systems.client.SystemsClient;
import edu.utexas.tacc.tapis.systems.client.gen.model.ReqUnlinkChildren;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;


public class SystemUnlinkChildren {
    public static void main(String[] args) throws Exception
    {
        String className = SystemsGet.class.getSimpleName();
        //----------------------- INITIALIZE PARMS -----------------------//
        CMDUtilsParameters parms = null;
        try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
            throw new Exception(String.format("Parms initialization for %s has failed with Exception: %s", className ,e));
        }

        //----------------------- VALIDATE PARMS -----------------------//
        if(parms.jwtFilename == null)
            throw new Exception("jwtFilename is null and is required for SystemGet operation, THROWING ERROR");

        //----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = DriverUtils.getTestProfile(parms.jwtFilename);

        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create the app.
        var sysClient = new SystemsClient(props.getProperty("BASE_URL"), props.getProperty("USER_JWT"));

        //----------------------- ASSIGN OBO USER AND TENANT -----------------------//
        if(parms.oboTenant != null)
            DriverUtils.setOboHeaders(sysClient, parms.oboUser, parms.oboTenant);

        //----------------------- USE CLIENT OBJECT -----------------------//
        // get the comma separated string as a list trimming whitespace off each name, and filtering
        // empty ones.
        List<String> childSystemNames = null;
        if(parms.childSystemNames != null) {
            childSystemNames = Arrays.asList(StringUtils.split(parms.childSystemNames, ","))
                    .stream().map(id -> id.trim()).filter(id -> !StringUtils.isBlank(id)).collect(Collectors.toList());
        }
        ReqUnlinkChildren reqUnlinkChildren = new ReqUnlinkChildren();
        reqUnlinkChildren.setChildSystemIds(childSystemNames);
        int count = sysClient.unlinkChildren(parms.systemName, parms.allChildren, reqUnlinkChildren);
        System.out.println("Systems updated: " + count);
    }
}