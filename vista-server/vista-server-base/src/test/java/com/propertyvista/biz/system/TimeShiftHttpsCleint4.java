/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 29, 2014
 * @author vlads
 */
package com.propertyvista.biz.system;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import com.pyx4j.config.server.ServerSideConfiguration;
import com.pyx4j.entity.rdb.cfg.Configuration.DatabaseType;

import com.propertyvista.biz.system.dev.TimeShiftX509TrustManager;
import com.propertyvista.config.tests.VistaTestsServerSideConfiguration;

public class TimeShiftHttpsCleint4 {

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {
        ServerSideConfiguration.setInstance(new VistaTestsServerSideConfiguration(DatabaseType.HSQLDB));
        SchemeRegistry registry = new SchemeRegistry();

        SSLSocketFactory sslSocketFactory;

        //sslSocketFactory = SSLSocketFactory.getSocketFactory();
        sslSocketFactory = new SSLSocketFactory(TimeShiftX509TrustManager.createTimeShiftSSLContext());

        registry.register(new Scheme("https", 443, sslSocketFactory));

        ClientConnectionManager connManager;
        connManager = new ThreadSafeClientConnManager(registry);

        DefaultHttpClient client = new DefaultHttpClient(connManager);

        HttpGet request = new HttpGet(new URI("https://www.google.com/"));

        HttpResponse httpResponse = client.execute(request);

        if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new Error(httpResponse.getStatusLine().getReasonPhrase());
        } else {
            System.out.println(httpResponse.getStatusLine().getStatusCode() + " " + httpResponse.getStatusLine().getReasonPhrase());
        }
    }

}
