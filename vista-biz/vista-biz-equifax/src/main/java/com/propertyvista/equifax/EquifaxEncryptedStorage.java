/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-12-31
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.equifax;

import java.nio.charset.Charset;

import com.pyx4j.commons.Key;
import com.pyx4j.config.server.ServerSideFactory;

import com.propertyvista.biz.system.encryption.EncryptedStorageFacade;
import com.propertyvista.server.domain.CustomerCreditCheckReport;

class EquifaxEncryptedStorage {

    static String decrypt(CustomerCreditCheckReport report) {
        byte[] xmlData = ServerSideFactory.create(EncryptedStorageFacade.class).decrypt(report.publicKey().getValue(), report.data().getValue());
        return new String(xmlData, Charset.forName("UTF-8"));
    }

    static void encrypt(CustomerCreditCheckReport report, String xml) {
        byte[] xmlData = xml.getBytes(Charset.forName("UTF-8"));
        Key publicKeyKey = ServerSideFactory.create(EncryptedStorageFacade.class).getCurrentPublicKey();
        byte[] encryptedData = ServerSideFactory.create(EncryptedStorageFacade.class).encrypt(publicKeyKey, xmlData);
        report.publicKey().setValue(publicKeyKey);
        report.data().setValue(encryptedData);
    }
}
