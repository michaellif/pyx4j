/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-12
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.server.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sun.jersey.core.util.Base64;

import com.pyx4j.config.server.ServerSideFactory;
import com.pyx4j.essentials.server.download.Downloadable;
import com.pyx4j.essentials.server.download.MimeMap;
import com.pyx4j.gwt.shared.DownloadFormat;

import com.propertyvista.biz.system.encryption.EncryptedStorageFacade;
import com.propertyvista.operations.rpc.encryption.EncryptedStorageDTO;
import com.propertyvista.operations.rpc.services.EncryptedStorageService;

public class EncryptedStorageServiceImpl implements EncryptedStorageService {

    @Override
    public void getSystemState(AsyncCallback<EncryptedStorageDTO> callback) {
        callback.onSuccess(ServerSideFactory.create(EncryptedStorageFacade.class).getSystemState());
    }

    @Override
    public void createNewKeyPair(AsyncCallback<String> callback, char[] password) {
        byte[] keyData = ServerSideFactory.create(EncryptedStorageFacade.class).createNewKeyPair(password);
        byte[] binaryDataAsText = Base64.encode(keyData);
        Downloadable d = new Downloadable(binaryDataAsText, MimeMap.getContentType(DownloadFormat.TXT));
        String fileName = "key-" + new SimpleDateFormat("YYYY-MM-dd").format(new Date()) + ".key";
        d.save(fileName);
        callback.onSuccess(fileName);
    }

}
