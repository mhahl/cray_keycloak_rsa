package org.rsa.authenticator;

import org.jboss.logging.Logger;
import org.keycloak.authentication.AuthenticationFlowContext;
import org.keycloak.authentication.AuthenticationFlowError;
import org.keycloak.forms.login.LoginFormsProvider;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.util.*;

import static org.rsa.authenticator.Const.*;


public class RSAAuthenticator implements org.keycloak.authentication.Authenticator {

    private static Logger _log = Logger.getLogger(RSAAuthenticator.class);

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

        int tokenCounter = 0;

        // Collect the messages for the tokens to display
        List<String> otpMessages = new ArrayList<>();
    
        // Create login form
        Response challenge = context.form()
                .setAttribute(FORM_OTP_MESSAGE, DEFAULT_OTP_MESSAGE)
                .setAttribute(FORM_USERNAME_MESSAGE, DEFAULT_USERNAME_MESSAGE)
                .setAttribute(FORM_SHARED_USERNAME, _sharedUsername.toString())
                .createForm(FORM_FILE_NAME);
        context.challenge(challenge);
    }

    /**
     * This function will be called if the user submitted the OTP form
     *
     * @param context AuthenticationFlowContext
     */
    @Override
    public void action(AuthenticationFlowContext context) {
        MultivaluedMap<String, String> formData = context.getHttpRequest().getDecodedFormParameters();
        if (formData.containsKey("cancel")) {
            context.resetFlow();
            return;
        }

        // Get data from form
        String otpMessage = formData.getFirst(FORM_OTP_MESSAGE);
        String otp = formData.getFirst(FORM_RSA_OTP);
        String rsaUsername = formData.getFirst(FORM_RSA_USERNAME);
         
        if (!_sharedUsername) {
            _currentUserName = rsaUsername;
        }

        if(otp == null || otp.isEmpty()) {
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

        if (!validateResponse(context, otp)) {
            LoginFormsProvider form = context.form()
                            .setAttribute(FORM_OTP_MESSAGE, DEFAULT_OTP_MESSAGE)
                            .setAttribute(FORM_USERNAME_MESSAGE, DEFAULT_USERNAME_MESSAGE)
                            .setAttribute(FORM_SHARED_USERNAME, _sharedUsername.toString());
            form.setError("Authentication failed.");
            
            Response challenge = form.createForm(FORM_FILE_NAME);
            context.failureChallenge(AuthenticationFlowError.INVALID_CREDENTIALS, challenge);
            return;
        }
        context.success();
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
