## Keycloak RSA plugin 

This plugin uses the RSA AM REST API to interact with your RSA AM instance. 
Currently, the keycloak login assumes that RSA and Keycloak share a username. 

## Configuration

This plugin is configured based on a set of env variables. The following are to be set prior to use:

| Configuration   | Explanation                                                                                                         |
|-----------------|---------------------------------------------------------------------------------------------------------------------|
| SERVER_URL      | This is the name of the RSA server e.g. https://my-rsa.test.com                                                     |
| VERIFY_ENDPOINT | This is the endpoint used to verify the user, this plugin defaults to using the endpoint /mfa/v1_1/authn/initialize |
| CLIENT_ID       | This is the FQHN of your instance e.g. my-rsa.test.com                                                              |
| CLIENT_KEY      | This is the Access Key that is provided by the RSA instance when you enable the API                                 |
| VERIFY_SSL      | This is a boolean value, set to TRUE to verify the SSL cert of the endpoint and vice versa                          |


## RSA Configuration

The following document describes enabling and using the RSA API: https://community.rsa.com/docs/DOC-76573


## RSA API Usage 

This instance refers to the section in the above document: Initialize (with SecurID)

Request:


```json
{
    "clientId": "apihost",
    "subjectName": "test01",
    "subjectCredentials": [
        {
            "methodId": "SECURID",
            "collectedInputs": [
                {
                    "name": "SECURID",
                    "value": "222222"
                }
            ]
        }
    ],
    "context": {
        "authnAttemptId": "",
        "messageId": "test5213021196242",
        "inResponseTo": ""
    }
}
```


## Build Plug-in and Copy Files to Keycloak

1. Run mvn package
2. Copy the file RSA-Plugin-Provider.jar to your keycloak instance 
   <keycloak>/standalone/deployments/RSA-Plugin-Provider.jar
3. Copy the template file in src/main/resources/theme-resources/RSAAuthenticator.frl
   to <keycloak>/themes/base/login/RSAAuthenticator.ftl