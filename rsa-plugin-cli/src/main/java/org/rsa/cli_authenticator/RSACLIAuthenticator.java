package org.rsa.cli_authenticator;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.events.Errors;
import org.keycloak.services.ErrorResponse;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.*;

import static org.rsa.cli_authenticator.Const.*;


public class RSACLIAuthenticator implements org.keycloak.authentication.Authenticator {


    private static Logger _log = Logger.getLogger(RSACLIAuthenticator.class);

    private String _currentUserName;
    private Boolean _sharedUsername;
    private Configuration _config;
    private Endpoint _endpoint;


    @Override
    public void authenticate(AuthenticationFlowContext context) {
        /**
         *  Enable the empty constructor to use environment variables 
         *  _config = new Configuration();
         */
        _config = new Configuration(context.getAuthenticatorConfig().getConfig());
        _endpoint = new Endpoint(_config);

        UserModel user = context.getUser();
        _currentUserName = user.getUsername();
        _sharedUsername = _config.getSharedUsername();

        // Collect the messages for the tokens to display
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        
        String rsaOtp = formData.getFirst(FORM_RSA_OTP);
        String rsaUsername = formData.getFirst(FORM_RSA_USERNAME);

        if (!_sharedUsername) {
            _currentUserName = rsaUsername;
        }


        if(rsaOtp == null || rsaOtp.isEmpty()) {
            JsonObject error =  Json.createObjectBuilder()
                      .add("error", "missing_parameter")
                      .add("error_description", "Missing parameter: rsa_token")
                      .build();
            Response challengeResponse = Response
                                .status(Response.Status.UNAUTHORIZED)
                                .entity(error)
                                .build();
            context.failure(AuthenticationFlowError.INVALID_USER, challengeResponse);
            return;
        }

        if(_currentUserName == null || _currentUserName.isEmpty()) {
            JsonObject error =  Json.createObjectBuilder()
                      .add("error", "missing_parameter")
                      .add("error_description", "Missing parameter: username")
                      .build();
            Response challengeResponse = Response
                                .status(Response.Status.UNAUTHORIZED)
                                .entity(error)
                                .build();
            context.failure(AuthenticationFlowError.INVALID_USER, challengeResponse);
            return;
        }

        if (!validateResponse(context, rsaOtp)) {
            Response challengeResponse = Response
                                            .status(Response.Status.UNAUTHORIZED)
                                            .entity("invalid_request")
                                            .build();
            context.failure(AuthenticationFlowError.INVALID_USER, challengeResponse);
            return;
        }

        context.success();
    }

    // /**
    //  * This function will be called if the user submitted the OTP form
    //  *
    //  * @param context AuthenticationFlowContext
    //  */
    @Override
    public void action(AuthenticationFlowContext context) {
    }

    /**
     * Check if authentication is successful
     *
     * @param context AuthenticationFlowContext
     * @return true if authentication was successful, else false
     */
    private boolean validateResponse(AuthenticationFlowContext context, String otp) {

        String verifyEndpoint = _config.getVerifyEndpoint();
        JsonObject params = buildPayload(otp);
        JsonObject body = _endpoint.sendRequest(verifyEndpoint, params, POST);
        try {
            String result = body.getString(RSA_ATTEMPT_RESPONSE);

            if (result.equals(SUCCESS)) {
                return  true;
            }
        } catch (Exception e) {
            _log.error("RSA tokencode verification failed.");
        }
        return false;
    }

    private JsonObject buildPayload(String otp) {

	    JsonObject body = Json.createObjectBuilder()
                      .add(KEY_CLIENT_ID, _config.getClientId())
                      .add(SUBJECT_NAME, _currentUserName)
                      .add(SUBJECT_CREDENTIALS, Json.createArrayBuilder()
                                              		.add(Json.createObjectBuilder()
                                                      .add(METHOD_ID, METHOD)
                                                      .add(COLLECTED_INPUTS, Json.createArrayBuilder()
                                                    		  .add(Json.createObjectBuilder()
                                                                  .add(NAME, METHOD)
                                                                  .add(VALUE, otp)
                                                    			)
                                                      )
                                                  )
                      )
                      .add(CONTEXT, Json.createObjectBuilder()
                                  	.add(AUTH_ATTEMPT_ID_DESC, AUTH_ATTEMPT_ID)
                                  	.add(MESSAGE_ID_DESC, getMessageId())
                                  	.add(IN_RESPONSE_TO_DESC, IN_RESPONSE_TO)
                      ).build();


        return body;
    }

    private String getMessageId() {
        return "test";
    }

    @Override
    public boolean requiresUser() {
        return true;
    }

    @Override
    public boolean configuredFor(KeycloakSession session, RealmModel realm, UserModel user) {
        return true;
    }

    @Override
    public void setRequiredActions(KeycloakSession session, RealmModel realm, UserModel user) {
    }

    @Override
    public void close() {
    }
}
