package org.rsa.authenticator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.rsa.authenticator.Const.*;

class Configuration {

    private String _serverURL;
    private String _verifyEndpoint;
    private String _clientId;
    private String _clientKey;
    private String _verifySSL;
    private String _sharedUsername;


    Configuration(Map<String, String> configMap) {
        _serverURL      = getConfig(configMap, SERVER_URL);
        _verifyEndpoint = getConfig(configMap, VERIFY_ENDPOINT);
        _clientId       = getConfig(configMap, CLIENT_ID);
        _clientKey      = getConfig(configMap, CLIENT_KEY);
        _verifySSL      = getConfig(configMap, VERIFY_SSL);
        _sharedUsername = getConfig(configMap, SHARED_USERNAME);
    }

    Configuration(String _serverURL, String _verifyEndpoint, String _clientId, 
        String _clientKey, String _verifySSL, String _sharedUsername) {
        this._serverURL      = _serverURL;
        this._verifyEndpoint = _verifyEndpoint;
        this._clientId       = _clientId;
        this._clientKey      = _clientKey;
        this._verifySSL      = _verifySSL;
        this._sharedUsername = _sharedUsername;
    }

    Configuration() {
        _serverURL      = getEnv(SERVER_URL);
        _verifyEndpoint = getEnv(VERIFY_ENDPOINT);
        _clientId       = getEnv(CLIENT_ID);
        _clientKey      = getEnv(CLIENT_KEY);
        _verifySSL      = getEnv(VERIFY_SSL);
        _sharedUsername = getEnv(SHARED_USERNAME);
    }

    String getServerURL() {
        return _serverURL;
    }

    String getVerifyEndpoint() {
        return _verifyEndpoint.equals("") ? DEFAULT_VERIFY_ENDPOINT : _verifyEndpoint;
    }
    
    String getClientId() {
    	return _clientId;
    }
    
    String getClientKey() {
    	return _clientKey;
    }

    Boolean getVerifySSL() {
    	return _verifySSL.toLowerCase().equals("true") ? true : false;
    }

    Boolean getSharedUsername() {
    	return _sharedUsername.toLowerCase().equals("true") ? true : false;
    }

    String getEnv(String key) {
        String _env;
        if (key != null){
            _env = System.getenv(key);
            return _env != null ? _env : "";
        }
		return "";
    }

    String getConfig(Map<String, String> configMap, String key) {
        String _env;
        if (key != null){
            _env = configMap.get(key);
            return _env != null ? _env : "";
        }
		return "";
    }
}