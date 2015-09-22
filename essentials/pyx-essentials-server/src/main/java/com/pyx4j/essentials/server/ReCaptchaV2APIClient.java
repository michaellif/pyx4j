/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Sep 22, 2015
 * @author vlads
 */
package com.pyx4j.essentials.server;

import java.net.HttpURLConnection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jsonp.JsonProcessingFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.pyx4j.commons.RuntimeExceptionSerializable;
import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.server.contexts.ServerContext;

public class ReCaptchaV2APIClient {

    private final static Logger log = LoggerFactory.getLogger(ReCaptchaV2APIClient.class);

    private static final I18n i18n = I18n.get(ReCaptchaV2APIClient.class);

    static class GoogleVerificationRequest {

        public String secret;

        public String response;

        public String remoteip;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class GoogleVerificationResponse {

        public boolean success;

        @JsonProperty("error-codes")
        public String[] errorCodes;

    }

    private Client client;

    private WebTarget webTarget;

    private String privateKey;

    private final boolean enableLogging = false;

    private ReCaptchaV2APIClient() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.register(JsonProcessingFeature.class);
        clientConfig.register(JacksonFeature.class);
        if (enableLogging) {
            clientConfig.register(new LoggingFilter(java.util.logging.Logger.getLogger(ReCaptchaV2APIClient.class.getName()), true));
        }
        client = ClientBuilder.newClient(clientConfig);
        webTarget = client.target("https://www.google.com/recaptcha/api");

        privateKey = ServerSideConfiguration.instance(EssentialsServerSideConfiguration.class).getReCaptchaPrivateKey();
    }

    private static class SingletonHolder {
        public static final ReCaptchaV2APIClient INSTANCE = new ReCaptchaV2APIClient();
    }

    public static ReCaptchaV2APIClient instance() {
        return SingletonHolder.INSTANCE;
    }

    public void assertCaptcha(String userResponseToken) {
        GoogleVerificationRequest request = new GoogleVerificationRequest();
        request.response = userResponseToken;
        request.secret = privateKey;
        request.remoteip = ServerContext.getRequestRemoteAddr();

        Response response = webTarget.path("siteverify")//
                .request(MediaType.APPLICATION_JSON).post(Entity.json(request));
        if (response.getStatus() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeExceptionSerializable(i18n.tr("reCAPTCHA Connection Failed"));
        }
        GoogleVerificationResponse rc = response.readEntity(GoogleVerificationResponse.class);
        if (!rc.success) {
            if (rc.errorCodes != null) {
                log.error("reCAPTCHAv2 configuration error {}", (Object) rc.errorCodes);
            }
            throw new UserRuntimeException(i18n.tr("The CAPTCHA Solution You Entered Was Incorrect"));
        }
    }

}
