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
 * @version $Id$
 */
package com.propertyvista.payment.caledon;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.payment.PaymentProcessingException;
import com.propertyvista.payment.SystemConfig;

import com.pyx4j.commons.CommonsStringUtils;

class CaledonHttpClient {

    private final static Logger log = LoggerFactory.getLogger(CaledonHttpClient.class);

    private final String url = "https://lt3a.caledoncard.com/";

    private static SystemConfig configuration = new SystemConfig();

    CaledonResponse transaction(CaledonRequest request) {

        GetMethod httpMethod = new GetMethod(url);
        httpMethod.setFollowRedirects(false);
        httpMethod.setQueryString(buildRequestQuery(request));
        //System.out.println(httpMethod.getQueryString());

        HttpClient httpClient = new HttpClient();
        if (CommonsStringUtils.isStringSet(configuration.getProxyHost())) {
            httpClient.getHostConfiguration().setProxy(configuration.getProxyHost(), configuration.getProxyPort());
        }

        try {
            int httpResponseCode = httpClient.executeMethod(httpMethod);
            if (httpResponseCode != HttpURLConnection.HTTP_OK) {
                throw new PaymentProcessingException("Unexpected rerver Response " + httpResponseCode);
            }
            String tmp = httpMethod.getResponseBodyAsString();
            System.out.println(tmp);
            if (!tmp.contains("TEXT=CARD OK")) {
                throw new PaymentProcessingException("TODO [" + tmp + "]");
            }

        } catch (HttpException e) {
            log.error("transaction protocol error", e);
            throw new PaymentProcessingException("Protocol error occurs", e);
        } catch (IOException e) {
            log.error("transaction transport error", e);
            throw new PaymentProcessingException("Transport error occurs", e);
        }

        CaledonResponse rc = new CaledonResponse();
        return new CaledonResponse();
    }

    private NameValuePair[] buildRequestQuery(CaledonRequest request) {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        for (Field field : request.getClass().getDeclaredFields()) {
            HttpRequestField nameDeclared = field.getAnnotation(HttpRequestField.class);
            if (nameDeclared == null) {
                continue;
            }
            try {
                Object value = field.get(request);
                if (value != null) {
                    params.add(new NameValuePair(nameDeclared.value(), value.toString()));
                }
            } catch (Exception e) {
                log.error("object value access error", e);
                throw new PaymentProcessingException("System error", e);
            }
        }

        return params.toArray(new NameValuePair[params.size()]);
    }
}
