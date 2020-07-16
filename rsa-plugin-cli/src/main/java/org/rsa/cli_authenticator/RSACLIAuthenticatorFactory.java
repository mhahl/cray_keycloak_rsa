package org.rsa.cli_authenticator;

import org.keycloak.Config;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

import static org.rsa.cli_authenticator.Const.*;

/**
 * Copyright 2019 NetKnights GmbH - micha.preusser@netknights.it
 * nils.behlen@netknights.it
 * - Modified
 * <p>
 * Based on original code:
 * <p>
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class RSACLIAuthenticatorFactory implements org.keycloak.authentication.AuthenticatorFactory {

    private static final RSACLIAuthenticator SINGLETON = new RSACLIAuthenticator();
    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public org.keycloak.authentication.Authenticator create(KeycloakSession session) {
        return SINGLETON;
    }

    private static AuthenticationExecutionModel.Requirement[] REQUIREMENT_CHOICES = {
            AuthenticationExecutionModel.Requirement.REQUIRED,
            AuthenticationExecutionModel.Requirement.DISABLED
    };

    @Override
    public AuthenticationExecutionModel.Requirement[] getRequirementChoices() {
        return REQUIREMENT_CHOICES;
    }

     static {
        ProviderConfigProperty _serverURL = new ProviderConfigProperty();
        _serverURL.setType(ProviderConfigProperty.TEXT_TYPE);
        _serverURL.setName(SERVER_URL);
        _serverURL.setLabel("RSA URL");
        _serverURL.setHelpText("The complete url of your RSA Authentication Manager instance with its configured API port: \"https://<RSA>:port\")");
        configProperties.add(_serverURL);

        ProviderConfigProperty _verifyEndpoint = new ProviderConfigProperty();
        _verifyEndpoint.setType(ProviderConfigProperty.TEXT_TYPE);
        _verifyEndpoint.setName(VERIFY_ENDPOINT);
        _verifyEndpoint.setLabel("RSA Verify Endpoint");
        _verifyEndpoint.setHelpText("The RSA API authenticaiton verify endpoint, e.g. /mfa/auth_1/verify");
        configProperties.add(_verifyEndpoint);

        ProviderConfigProperty _clientId = new ProviderConfigProperty();
        _clientId.setType(ProviderConfigProperty.STRING_TYPE);
        _clientId.setName(CLIENT_ID);
        _clientId.setLabel("Keycloak Client ID");
        _clientId.setHelpText("Keycloak Client ID being used for Direct Grant authentication.");
        configProperties.add(_clientId);

        ProviderConfigProperty _clientKey = new ProviderConfigProperty();
        _clientKey.setType(ProviderConfigProperty.PASSWORD);
        _clientKey.setName(CLIENT_KEY);
        _clientKey.setLabel("RSA Authentication Manager Client Key");
        _clientKey.setHelpText("RSA AM Client Key");
        configProperties.add(_clientKey);

        ProviderConfigProperty _verifySSL = new ProviderConfigProperty();
        _verifySSL.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        _verifySSL.setName(VERIFY_SSL);
        _verifySSL.setLabel("Verify SSL");
        _verifySSL.setHelpText("Verify the SSL certificate of the RSA instance?");
        configProperties.add(_verifySSL);

        ProviderConfigProperty _sharedUsername = new ProviderConfigProperty();
        _sharedUsername.setType(ProviderConfigProperty.BOOLEAN_TYPE);
        _sharedUsername.setName(SHARED_USERNAME);
        _sharedUsername.setLabel("Shared username");
        _sharedUsername.setHelpText("Check if username is shared between Keycloak and RSA");
        configProperties.add(_sharedUsername);
    }


    @Override
    public boolean isUserSetupAllowed() {
        return false;
    }

    @Override
    public boolean isConfigurable() {
        return true;
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getHelpText() {
        return "Authenticate the second factor against RSA.";
    }

    @Override
    public String getDisplayType() {
        return "RSA-CLI";
    }

    @Override
    public String getReferenceCategory() {
        return "RSA-CLI";
    }

    @Override
    public void init(Config.Scope config) {
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {
    }

    @Override
    public void close() {
    }
}
