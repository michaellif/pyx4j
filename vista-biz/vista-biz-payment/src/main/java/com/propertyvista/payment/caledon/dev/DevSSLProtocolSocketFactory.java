/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-06-26
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.payment.caledon.dev;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.HttpClientError;
import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.params.HttpConnectionParams;

public class DevSSLProtocolSocketFactory extends EasySSLProtocolSocketFactory {

    private SSLContext sslcontext = null;

    private static SSLContext createEasySSLContext() {
        try {
            SSLContext localSSLContext = SSLContext.getInstance("SSL");
            localSSLContext.init(null, new TrustManager[] { new DevX509TrustManager(null) }, null);
            return localSSLContext;
        } catch (Exception localException) {
            throw new HttpClientError(localException.toString());
        }
    }

    private SSLContext getSSLContext() {
        if (this.sslcontext == null)
            this.sslcontext = createEasySSLContext();
        return this.sslcontext;
    }

    @Override
    public Socket createSocket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(paramString, paramInt1, paramInetAddress, paramInt2);
    }

    @Override
    public Socket createSocket(String paramString, int paramInt1, InetAddress paramInetAddress, int paramInt2, HttpConnectionParams paramHttpConnectionParams)
            throws IOException, UnknownHostException, ConnectTimeoutException {
        if (paramHttpConnectionParams == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        int i = paramHttpConnectionParams.getConnectionTimeout();
        SSLSocketFactory localSSLSocketFactory = getSSLContext().getSocketFactory();
        if (i == 0) {
            return localSSLSocketFactory.createSocket(paramString, paramInt1, paramInetAddress, paramInt2);
        }
        Socket localSocket = localSSLSocketFactory.createSocket();
        InetSocketAddress localInetSocketAddress1 = new InetSocketAddress(paramInetAddress, paramInt2);
        InetSocketAddress localInetSocketAddress2 = new InetSocketAddress(paramString, paramInt1);
        localSocket.bind(localInetSocketAddress1);
        localSocket.connect(localInetSocketAddress2, i);
        return localSocket;
    }

    @Override
    public Socket createSocket(String paramString, int paramInt) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(paramString, paramInt);
    }

    @Override
    public Socket createSocket(Socket paramSocket, String paramString, int paramInt, boolean paramBoolean) throws IOException, UnknownHostException {
        return getSSLContext().getSocketFactory().createSocket(paramSocket, paramString, paramInt, paramBoolean);
    }

    @Override
    public boolean equals(Object paramObject) {
        return ((paramObject != null) && (paramObject.getClass().equals(EasySSLProtocolSocketFactory.class)));
    }

    @Override
    public int hashCode() {
        return EasySSLProtocolSocketFactory.class.hashCode();
    }
}
