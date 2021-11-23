package edu.utexas.tacc.tapis.cmd.driver;

import java.lang.reflect.Type;
import java.util.Properties;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import edu.utexas.tacc.tapis.shared.utils.TapisGsonUtils;
import edu.utexas.tacc.tapis.tokens.client.TokensClient;
import edu.utexas.tacc.tapis.tokens.client.gen.model.InlineObject1.AccountTypeEnum;
import edu.utexas.tacc.tapis.tokens.client.model.CreateTokenParms;
import edu.utexas.tacc.tapis.tokens.client.model.TokenResponsePackage;

public class TokensCreateServiceToken 
{
    /** Creates a Tapis app from a request file
     * 
     * AppCreate -jwt <jwt filename located in $HOME/Tapis-cmd/jwt> -req <name of json request file located in driver/requests>
     */
    public static void main(String[] args) throws Exception
    {
    	//----------------------- INITIALIZE PARMS -----------------------//
    	CMDUtilsParameters parms = null;
    	try {parms = new CMDUtilsParameters(args);}
        catch (Exception e) {
          throw new Exception("Parms initialization for appCreate has failed with Exception: ",e);
        }
    	
    	//----------------------- VALIDATE PARMS -----------------------//
    	if(parms.reqFilename == null)
    		throw new Exception("reqFilename is null and is required for appCreate, THROWING ERROR");
    	
    	if(parms.jwtFilename == null)
    		throw new Exception("jwtFilename is null and is required for appCreate, THROWING ERROR");
        
    	System.out.println("Processing " + parms.reqFilename);
    	
        //----------------------- READ JSON REQUEST INTO REQ OBJECT -----------------------//
        // Convert json string into an app create request.
        var gsonBuilder = TapisGsonUtils.getGsonBuilder(false);
        Type ACCOUNT_TYPE = new TypeToken<AccountTypeEnum>(){}.getType();
        gsonBuilder.registerTypeAdapter(ACCOUNT_TYPE, new AccountTypeAdapter());
        var gson = gsonBuilder.create();
    	CreateTokenParms tokReq = gson.fromJson(TestUtils.readRequestFile(parms.reqFilename), CreateTokenParms.class);
        
        //----------------------- READ IN JWT PROFILE -----------------------//
        // Read base url and jwt from file.
        Properties props = TestUtils.getTestProfile(parms.jwtFilename);
        
        //----------------------- CREATE CLIENT OBJECT -----------------------//
        // Create the app.
        var tokensClient = new TokensClient(props.getProperty("BASE_URL"), props.getProperty("USER"), parms.password);
  
        //----------------------- USE CLIENT OBJECT -----------------------//
        TokenResponsePackage resp = tokensClient.createToken(tokReq);
        System.out.println("Access token:\n\n" + resp.getAccessToken().getAccessToken());
    }
    
    // Deserialize hiddenOption enums so they can be loaded into the profile create request.
    private static final class AccountTypeAdapter
     implements JsonDeserializer<AccountTypeEnum>
    {
        @Override
        public AccountTypeEnum deserialize(JsonElement json, Type typeOf, JsonDeserializationContext arg2)
                throws JsonParseException 
        {
            return AccountTypeEnum.valueOf(json.getAsString());
        }
    }

}
