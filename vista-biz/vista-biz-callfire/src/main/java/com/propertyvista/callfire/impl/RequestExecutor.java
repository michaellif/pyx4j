/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 28, 2011
 * @author Mark Levitin
 * @version $Id$
 */
package com.propertyvista.callfire.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

public class RequestExecutor {

    private static final String APIKEY = "690124bd5ad1c562d56ffbaadb7f94139ab82fb6";

    private static final String WEBSERVER_PROTOCOL = "https";

    private static final String WEBSERVER_HOST = "www.callfire.com";

    private static final int WEBSERVER_PORT = 0;

    private static final String WEBSERVER_CONTEXT = "/cloud/1/";

    static public String createCampaign(String definition, String caller) throws URISyntaxException, ClientProtocolException, IOException {

        URI uri = URIUtils.createURI(WEBSERVER_PROTOCOL, WEBSERVER_HOST, WEBSERVER_PORT, WEBSERVER_CONTEXT + "callfirexml/campaign", null, null);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("apikey", APIKEY));
        params.add(new BasicNameValuePair("campaignName", "vista-callfire-campaign"));
        params.add(new BasicNameValuePair("isOutbound", "true"));
        params.add(new BasicNameValuePair("outboundCallerid", caller));
        params.add(new BasicNameValuePair("callfireXml", definition));

        HttpPost request = new HttpPost(uri);
        request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

        String reply = execute(request);

        if (reply == null || !reply.contains("<campaignid>") || !reply.contains("</campaignid>")) {
            return null;
        } else {
            return reply.replaceFirst("^.*<campaignid>", "").replaceFirst("</campaignid>.*$", "");
        }
    }

    static public boolean sendCalls(String campaignid, String numbers) throws URISyntaxException, ClientProtocolException, IOException {

        URI uri = URIUtils.createURI("https", WEBSERVER_HOST, WEBSERVER_PORT, WEBSERVER_CONTEXT + "campaign/" + campaignid + "/call", null, null);

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("apikey", APIKEY));
        params.add(new BasicNameValuePair("numbers", numbers));

        HttpPost request = new HttpPost(uri);
        request.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

        String reply = execute(request);

        return reply != null;
    }

    private static String execute(HttpRequestBase request) throws ClientProtocolException, IOException {

        SchemeRegistry schemeRegistry = new SchemeRegistry();

        // http scheme
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        // https scheme
        schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));

        HttpParams params = new BasicHttpParams();
        params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 1);
        params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(1));
        params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        ClientConnectionManager manager = new SingleClientConnManager(params, schemeRegistry);

        // ignore that the ssl cert is self signed
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(new AuthScope(WEBSERVER_HOST, AuthScope.ANY_PORT), new UsernamePasswordCredentials("username", "password"));

        HttpContext httpContext = new BasicHttpContext();
        httpContext.setAttribute("http.auth.credentials-provider", provider);

        HttpClient httpClient = new DefaultHttpClient(manager, params);

        HttpResponse response = httpClient.execute(request, httpContext);

        String reply = null;

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            reply = convertStreamToString(response.getEntity().getContent());
        }

        httpClient.getConnectionManager().shutdown();

        return reply;
    }

    private static String convertStreamToString(InputStream is) throws IOException {
        if (is != null) {
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, HTTP.UTF_8));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}
