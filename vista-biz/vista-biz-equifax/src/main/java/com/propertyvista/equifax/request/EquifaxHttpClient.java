/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-23
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.equifax.request;

import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.equifax.uat.from.EfxTransmit;
import ca.equifax.uat.to.CNConsAndCommRequestType;

import com.pyx4j.essentials.j2se.HostConfig.ProxyConfig;
import com.pyx4j.essentials.j2se.util.MarshallUtil;

import com.propertyvista.config.SystemConfig;

public class EquifaxHttpClient {

    private final static Logger log = LoggerFactory.getLogger(EquifaxHttpClient.class);

    public static String serverUrl = "https://uat.equifax.ca/sts/processinquiry.asp";

    public static EfxTransmit execute(CNConsAndCommRequestType requestMessage) throws Exception {
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(serverUrl);

        log.debug("Connect to: {}", serverUrl);
        ProxyConfig proxy = SystemConfig.instance().getCaledonProxy();
        if (proxy != null) {
            log.debug("use proxy {}", proxy.getHost());
            if (proxy.getUser() != null) {
                httpclient.getCredentialsProvider().setCredentials(new AuthScope(proxy.getHost(), proxy.getPort()),
                        new UsernamePasswordCredentials(proxy.getUser(), proxy.getPassword()));
            }
            HttpHost httpProxyHost = new HttpHost(proxy.getHost(), proxy.getPort());
            httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, httpProxyHost);
        }

        try {
            QName qname = new QName("http://www.equifax.ca/XMLSchemas/CustToEfx", "CNCustTransmitToEfx");
            JAXBElement<CNConsAndCommRequestType> element = new JAXBElement<CNConsAndCommRequestType>(qname, CNConsAndCommRequestType.class, requestMessage);

            JAXBContext context = JAXBContext.newInstance(CNConsAndCommRequestType.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            StringWriter xml = new StringWriter();
            m.marshal(element, xml);

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("InputSegments", xml.toString()));
            nvps.add(new BasicNameValuePair("cmdSubmit", "Submit"));

            httpPost.setEntity(new UrlEncodedFormEntity(nvps));

            HttpResponse response = httpclient.execute(httpPost);

            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String xmlResponse = EntityUtils.toString(entity, Charset.forName("UTF-8"));
                EfxTransmit efxResponse = MarshallUtil.unmarshal(EfxTransmit.class, xmlResponse);
                return efxResponse;
            } else {
                log.error("transaction protocol error {}{}", response, response.getStatusLine());
                throw new RuntimeException(responseCode + ":" + response.getStatusLine());
            }
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }

}
