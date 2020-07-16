package org.rsa.authenticator;

import javax.json.Json;
import javax.json.JsonObject;

import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import org.rsa.authenticator.Endpoint;
import org.rsa.authenticator.Configuration;

import static org.rsa.authenticator.Const.*;

public class RSATest {

   private Configuration config;
   private Endpoint endpoint;
   private String _testServerURL;
   private String _testVerifyEndpoint;
   private String _testClientId;
   private String _testClientKey;
   private String _testVerifySSL;
   private String _testSharedUsername;

   public RSATest() {
    // override env vars
    _testServerURL      = "https://postman-echo.com";
    _testVerifyEndpoint = "/get";
    _testClientId       = "X";
    _testClientKey      = "X";
    _testVerifySSL      = "false";
    _testSharedUsername = "false";

    config = new Configuration(_testServerURL, 
                               _testVerifyEndpoint, 
                               _testClientId, 
                               _testClientKey, 
                               _testVerifySSL,
                               _testSharedUsername);

    endpoint = new Endpoint(config);
   }

    @Test
    public void configurationTest(){
        Assert.assertEquals(config.getServerURL(), _testServerURL);
        Assert.assertEquals(config.getVerifyEndpoint(), _testVerifyEndpoint);
        Assert.assertEquals(config.getClientId(), _testClientId);
        Assert.assertEquals(config.getClientKey(), _testClientKey);
        Assert.assertEquals(config.getVerifySSL(), _testVerifySSL == "false" ? false : true);
        Assert.assertEquals(config.getSharedUsername(), _testSharedUsername == "false" ? false : true);
    }

    @Test
    public void connectHttpsURLTest() {
        // Only a valid request will return
        JsonObject body = endpoint.sendRequest(_testVerifyEndpoint, null, GET);
        Assert.assertTrue(body != null);
    }

}
