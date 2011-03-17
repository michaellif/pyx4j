/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.server.pt.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.pt.ApplicationDocument.DocumentType;
import com.propertyvista.portal.rpc.pt.ApplicationDocumentsList;
import com.propertyvista.portal.rpc.pt.services.ApplicationDocumentsService;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.rpc.shared.VoidSerializable;

public class ApplicationDocumentsServiceImpl implements ApplicationDocumentsService {

    @Override
    public void retrieveAttachments(AsyncCallback<ApplicationDocumentsList> callback, Long tenantId, DocumentType documentType) {
        // TODO Auto-generated method stub
        ApplicationDocumentsList listHolder = EntityFactory.create(ApplicationDocumentsList.class);
        callback.onSuccess(listHolder);
    }

    @Override
    public void removeAttachment(AsyncCallback<VoidSerializable> callback, Long applicationDocumentId) {
        // TODO Auto-generated method stub
        callback.onSuccess(null);
    }

}
