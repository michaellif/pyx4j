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

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.commons.httpclient.contrib.ssl.EasyX509TrustManager;

public class DevX509TrustManager extends EasyX509TrustManager {

    public DevX509TrustManager(KeyStore paramKeyStore) throws NoSuchAlgorithmException, KeyStoreException {
        super(paramKeyStore);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] paramArrayOfX509Certificate, String paramString) throws CertificateException {
    }

}
