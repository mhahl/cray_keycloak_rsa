package org.rsa.authenticator;

import java.util.Arrays;
import java.util.List;

final class Const {
    
    private Const() {
    }

    static final String PROVIDER_ID = "rsa-browser-authenticator";

    // REST Methods 
    static final String GET = "GET";
    static final String POST = "POST";
    static final String TRUE = "true";

    // Environment variables
    static final String VERIFY_SSL = "VERIFY_SSL";
    static final String CLIENT_ID = "CLIENT_ID";
    static final String CLIENT_KEY = "CLIENT_KEY";
    static final String SERVER_URL = "SERVER_URL";
    static final String VERIFY_ENDPOINT = "VERIFY_ENDPOINT";
    static final String SHARED_USERNAME = "SHARED_USERNAME";

    static final String DEFAULT_VERIFY_ENDPOINT = "/mfa/v1_1/authn/initialize";

    // Template variables
    static final String FORM_FILE_NAME = "RSAAuthenticator.ftl";
    static final String DEFAULT_OTP_MESSAGE = "Please enter your RSA tokencode";
    static final String DEFAULT_USERNAME_MESSAGE = "Please enter your RSA username";
    static final String FORM_OTPTOKEN = "otpToken";
    static final String FORM_OTP_MESSAGE = "otpMessage";
    static final String FORM_USERNAME_MESSAGE = "usernameMessage";
    static final String FORM_SHARED_USERNAME = "sharedUsername";
    static final String FORM_RSA_OTP = "rsa_otp";
    static final String FORM_RSA_USERNAME = "rsa_username";

    // RSA Response
    static final String SUCCESS = "SUCCESS";
    
    // RSA pay load 
    static final String KEY_CLIENT_ID = "clientId";
    static final String KEY_CLIENT_KEY = "client-key";
    static final String SUBJECT_NAME = "subjectName";
    static final String SUBJECT_CREDENTIALS = "subjectCredentials";
    static final String METHOD_ID = "methodId";
    static final String METHOD = "SECURID";
    static final String COLLECTED_INPUTS = "collectedInputs";
    static final String NAME = "name";
    static final String VALUE = "value";
    static final String CONTEXT = "context";
    static final String RSA_ATTEMPT_RESPONSE = "attemptResponseCode";
    static final String AUTH_ATTEMPT_ID_DESC = "authnAttemptId";
    static final String AUTH_ATTEMPT_ID = "";
    static final String MESSAGE_ID_DESC = "messageId";
    static final String IN_RESPONSE_TO_DESC = "inResponseTo";
    static final String IN_RESPONSE_TO = "";

}