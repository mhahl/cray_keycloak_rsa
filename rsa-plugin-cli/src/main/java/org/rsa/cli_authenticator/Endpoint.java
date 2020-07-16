package org.rsa.cli_authenticator;

import org.jboss.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.rsa.cli_authenticator.Const.*;

class Endpoint {

    private Logger _log = Logger.getLogger(getClass().getName());
    private String _authToken;
    private Configuration _config;
    private List<String> excludedEndpointPrints = Collections.emptyList(); //Arrays.asList(ENDPOINT_AUTH);

    Endpoint(Configuration config) {
        this._config = config;
    }

    /**
     * Make a http(s) call to the specified path, the URL is taken from the config.
     * If SSL Verification is turned off in the config, the endpoints certificate will not be verified.
     *
     * @param path              Path to the API endpoint
     * @param params            All necessary parameters for request
     * @param authTokenRequired whether the authorization header should be set
     * @param method            "POST" or "GET"
     * @return JsonObject body which contains the whole response
     */

    JsonObject sendRequest(String path, JsonObject payload, String method) {

            try {
                URL rsaServerURL;

                rsaServerURL = new URL(_config.getServerURL() + path);

                HttpURLConnection con;
            
                if (rsaServerURL.getProtocol().equals("https")) {
                    con = (HttpsURLConnection) rsaServerURL.openConnection();
                } else {
                    con = (HttpURLConnection) rsaServerURL.openConnection();
                }

                if (!_config.getVerifySSL() && con instanceof HttpsURLConnection) {
                    con = turnOffSSLVerification((HttpsURLConnection) con);
                }

                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty(KEY_CLIENT_KEY, _config.getClientKey());

                con.setDoOutput(true);
                con.setRequestMethod(method);

                if (method.equals(POST)){
                    OutputStream os = con.getOutputStream();
                    os.write(payload.toString().getBytes());
                    os.close();
                }

                int responseCode = con.getResponseCode();


                if (responseCode == HttpURLConnection.HTTP_OK) { // success

                    BufferedReader in = new BufferedReader(new InputStreamReader(
                            con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    JsonReader jsonReader = Json.createReader(new StringReader(response.toString()));
                    JsonObject body = jsonReader.readObject();
                    jsonReader.close();

                    return body;
                } else {
                    return null;
                }

            } catch (Exception e) {
                _log.error(e);
            }
        return null;
    }
    
    private HttpsURLConnection turnOffSSLVerification(HttpsURLConnection con) {
        final TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return new java.security.cert.X509Certificate[]{};
                    }
                }
        };
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        if (sslContext == null) {
            return con;
        }

        final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
        con.setSSLSocketFactory(sslSocketFactory);
        con.setHostnameVerifier((hostname, session) -> true);

        return con;
    }

}