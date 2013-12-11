/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 13, 2013
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.caledon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
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
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.essentials.j2se.HostConfig.ProxyConfig;
import com.pyx4j.gwt.server.IOUtils;

import com.propertyvista.config.AbstractVistaServerSideConfiguration;
import com.propertyvista.config.SystemConfig;
import com.propertyvista.config.VistaSystemsSimulationConfig;
import com.propertyvista.payment.PaymentProcessingException;

public class CaledonHttpClientFee {

    private final static Logger log = LoggerFactory.getLogger(CaledonHttpClientFee.class);

    private final boolean debug = true;

    private final String urlProd = "https://portal.caledoncard.com/convfee_testing/";

    public CaledonFeeCalulationResponse transaction(CaledonFeeCalulationRequest request) {
        return transaction(request, new CaledonFeeCalulationResponse());
    }

    public CaledonPaymentWithFeeResponse transaction(CaledonPaymentWithFeeRequest request) {
        return transaction(request, new CaledonPaymentWithFeeResponse());
    }

    private <E extends CaledonFeeResponseBase> E transaction(Object request, E responseInstance) {
        String url;
        boolean useCardServiceSimulator = VistaSystemsSimulationConfig.getConfiguration().useCardServiceSimulator().getValue(Boolean.FALSE);
        if (useCardServiceSimulator) {
            url = ServerSideConfiguration.instance(AbstractVistaServerSideConfiguration.class).getBankingSimulatorConfiguration().getCardServiceSimulatorUrl()
                    + "/convfee/";
        } else {
            url = urlProd;
        }

        PostMethod httpMethod = new PostMethod(url);
        httpMethod.setFollowRedirects(false);
        httpMethod.addParameters(buildRequestQuery(request));
        httpMethod.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));

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

        try {
            int httpResponseCode = httpClient.executeMethod(httpMethod);
            if (httpResponseCode != HttpURLConnection.HTTP_OK) {
                throw new PaymentProcessingException("Unexpected server response " + httpResponseCode);
            }
            return buildResponse(getResponseBodyAsString(httpMethod), responseInstance);
        } catch (HttpException e) {
            log.error("transaction protocol error", e);
            throw new PaymentProcessingException("Protocol error occurs", e);
        } catch (IOException e) {
            log.error("transaction transport error", e);
            throw new PaymentProcessingException("Transport error occurs", e);
        }
    }

    private String getResponseBodyAsString(PostMethod httpMethod) throws IOException {
        InputStream in = httpMethod.getResponseBodyAsStream();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            IOUtils.copyStream(in, out, 1024);
            return out.toString(StandardCharsets.UTF_8.name());
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    private NameValuePair[] buildRequestQuery(Object request) {
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

        pairs.add(0, new NameValuePair("TESTING", "1"));
        if (debug) {
            log.debug("Request {}", pairs);
        }

        return pairs.toArray(new NameValuePair[pairs.size()]);
    }

    private <E extends CaledonFeeResponseBase> E buildResponse(String responseBody, E response) {
        log.debug("card transaction response body {}", responseBody);
        if (responseBody.length() == 0) {
            throw new PaymentProcessingException("Response is empty");
        }
        response.responseBody = responseBody;

        Map<String, String> values = new HashMap<String, String>();
        String[] nameValues = responseBody.split("&");
        if (nameValues.length > 0) {
            for (int i = 0; i < nameValues.length; i++) {
                String[] nameAndValue = nameValues[i].split("=");
                if (nameAndValue.length == 2) {
                    values.put(nameAndValue[0], nameAndValue[1]);
                } else {
                    log.warn("Can't pars argument '{}'", nameValues[i]);
                    throw new PaymentProcessingException("invalid Caledon response");
                }
            }
        }
        int tokenCount = 0;
        for (Field field : response.getClass().getFields()) {
            HttpResponseField nameDeclared = field.getAnnotation(HttpResponseField.class);
            if (nameDeclared == null) {
                continue;
            }
            String value = values.get(nameDeclared.value());
            if (value != null) {
                try {
                    field.set(response, value);
                } catch (Exception e) {
                    log.error("object {} value access error", field, e);
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
