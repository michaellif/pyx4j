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
 * @version $Id$
 */
package com.propertyvista.biz.system.dev;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class TimeShiftX509TrustManager implements X509TrustManager {

    private X509TrustManager standardTrustManager = null;

    public static SSLContext createTimeShiftSSLContext() throws Exception {
        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, new TrustManager[] { new TimeShiftX509TrustManager() }, null);
        return context;
    }

    public TimeShiftX509TrustManager() throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory factory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        factory.init((KeyStore) null);
        TrustManager[] trustmanagers = factory.getTrustManagers();
        if (trustmanagers.length != 1) {
            throw new NoSuchAlgorithmException("no trust manager found");
        }
        this.standardTrustManager = (X509TrustManager) trustmanagers[0];
    }

    @Override
    public void checkClientTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
        standardTrustManager.checkClientTrusted(certificates, authType);

    }

    @Override
    public void checkServerTrusted(X509Certificate[] certificates, String authType) throws CertificateException {
        ArrayList<X509Certificate> w = new ArrayList<>();
        for (X509Certificate c : certificates) {
            w.add(new TimeShiftX509Certificate(c));
        }
        standardTrustManager.checkServerTrusted(w.toArray(certificates), authType);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return this.standardTrustManager.getAcceptedIssuers();
    }

}
