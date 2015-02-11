/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-13
 * @author vlads
 */
package com.propertyvista.eft.caledoncards;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.j2se.HostConfig.ProxyConfig;

import com.propertyvista.biz.system.eft.PaymentProcessingException;
import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.SystemConfig;
import com.propertyvista.config.VistaSystemsSimulationConfig;
import com.propertyvista.eft.caledoncards.dev.DevSSLProtocolSocketFactory;

public class CaledonHttpClient {

    private final static Logger log = LoggerFactory.getLogger(CaledonHttpClient.class);

    private final boolean debug = true;

    private final boolean useTestServer = false;

    private final boolean easySSLEnabled = true;

    private final String urlTests = "https://216.185.82.37:1443/";

    private final String urlProd = "https://lt3a.caledoncard.com/";

    public CaledonResponse transaction(CaledonRequest request) {

        String url;

        boolean useCardServiceSimulator = VistaSystemsSimulationConfig.getConfiguration().useCardServiceSimulator().getValue(Boolean.FALSE);

        if (useCardServiceSimulator) {
            url = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getBankingSimulatorConfiguration().getCardServiceSimulatorUrl();
        } else {
            if (useTestServer) {
                url = urlTests;
            } else {
                url = urlProd;
            }
        }

        GetMethod httpMethod = new GetMethod(url);
        httpMethod.setFollowRedirects(false);
        httpMethod.setQueryString(caledoneQueryEncoding(buildRequestQuery(request)));
        httpMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));

        if (debug) {
            log.debug("Query: {}", querySafeToLog(httpMethod.getQueryString()));
        }

        HttpClient httpClient = new HttpClient();

        if (!useCardServiceSimulator) {
            ProxyConfig proxy = SystemConfig.instance().getCaledonProxy();
            if (proxy != null) {
                log.debug("use caledon proxy {}", proxy.getHost());
                httpClient.getHostConfiguration().setProxy(proxy.getHost(), proxy.getPort());
                if (proxy.getUser() != null) {
                    Credentials proxyCreds = new UsernamePasswordCredentials(proxy.getUser(), proxy.getPassword());
                    httpClient.getState().setProxyCredentials(AuthScope.ANY, proxyCreds);
                }
            }
        }

        if (useTestServer && easySSLEnabled) {
            @SuppressWarnings("deprecation")
            Protocol easyhttps = new Protocol("https", new DevSSLProtocolSocketFactory(), 1443);
            Protocol.registerProtocol("https", easyhttps);
        }

        try {
            int httpResponseCode = httpClient.executeMethod(httpMethod);
            if (httpResponseCode != HttpURLConnection.HTTP_OK) {
                throw new PaymentProcessingException("Unexpected server response " + httpResponseCode);
            }
            return buildResponse(httpMethod.getResponseBodyAsString());
        } catch (HttpException e) {
            log.error("transaction protocol error", e);
            throw new PaymentProcessingException("Protocol error occurs", e);
        } catch (IOException e) {
            log.error("transaction transport error", e);
            throw new PaymentProcessingException("Transport error occurs", e);
        }
    }

    private String querySafeToLog(String queryString) {
        return queryString.replaceAll("&CARD=\\d+", "&CARD=***").replaceAll("&CVV2=\\d+", "&CVV2=***");
    }

    private String caledoneQueryEncoding(NameValuePair[] pairs) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < pairs.length; i++) {
            if (i != 0) {
                b.append('&');
            }
            b.append(pairs[i].getName());
            b.append('=');
            b.append(caledoneQueryValueEncoding(pairs[i].getValue()));
        }
        return b.toString();
    }

    private String caledoneQueryValueEncoding(String value) {
        return value.replace("&", "&&");
    }

    private NameValuePair[] buildRequestQuery(CaledonRequest request) {
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();

        for (Field field : request.getClass().getFields()) {
            HttpRequestField nameDeclared = field.getAnnotation(HttpRequestField.class);
            if (nameDeclared == null) {
                continue;
            }
            try {
                Object value = field.get(request);
                if (value != null) {
                    NameValuePair nv = new NameValuePair(nameDeclared.value(), value.toString());
                    if (nameDeclared.first() && (pairs.size() != 0)) {
                        pairs.add(0, nv);
                    } else {
                        pairs.add(nv);
                    }
                }
            } catch (Exception e) {
                log.error("object value access error", e);
                throw new PaymentProcessingException("System error", e);
            }
        }

        return pairs.toArray(new NameValuePair[pairs.size()]);
    }

    private CaledonResponse buildResponse(String responseBody) {
        log.debug("card transaction response body {}", responseBody);
        if (responseBody.length() == 0) {
            throw new PaymentProcessingException("Response is empty");
        }
        CaledonResponse response = new CaledonResponse();
        Map<String, String> values = new HashMap<String, String>();
        String[] nameValues = responseBody.split("&");
        if (nameValues.length > 0) {
            for (int i = 0; i < nameValues.length; i++) {
                String[] nameAndValue = nameValues[i].split("=");
                if (nameAndValue.length == 2) {
                    values.put(nameAndValue[0], nameAndValue[1]);
                } else if (nameAndValue.length == 1) {
                    values.put(nameAndValue[0], "");
                    log.info("Received empty parameter '{}' from Caledon Response", nameValues[i]);
                } else {
                    log.warn("Can't pars argument '{}'", nameValues[i]);
                    throw new PaymentProcessingException("invalid Caledon response");
                }
            }
        }
        int tokenCount = 0;
        for (Field field : CaledonResponse.class.getFields()) {
            HttpResponseField nameDeclared = field.getAnnotation(HttpResponseField.class);
            if (nameDeclared == null) {
                continue;
            }
            String value = values.get(nameDeclared.value());
            if (value != null) {
                try {
                    field.set(response, value);
                } catch (Exception e) {
                    log.error("object value access error", e);
                    throw new PaymentProcessingException("System error", e);
                }
                tokenCount++;
            }
        }
        if (tokenCount == 0) {
            throw new PaymentProcessingException("Response is empty");
        }
        return response;
    }
}
