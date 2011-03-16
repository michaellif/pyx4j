/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 16, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.pt.services;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.propertyvista.portal.domain.pt.ApplicationDocument;
import com.propertyvista.portal.rpc.pt.ApplicationDocumentsList;

import com.pyx4j.rpc.shared.IService;

public interface ApplicationDocumentsService extends IService {

    /**
     * Ideally we want to return List<ApplicationDocument> but we want to bind the results
     * to EntityFolder for viewing in UI.
     */
    public void retrieveAttachments(AsyncCallback<ApplicationDocumentsList> callback, Long tenantId, ApplicationDocument.DocumentType documentType);
}
